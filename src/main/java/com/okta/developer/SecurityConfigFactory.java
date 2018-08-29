package com.okta.developer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.pac4j.core.client.Clients;
import org.pac4j.core.client.direct.AnonymousClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.http.client.direct.HeaderClient;
import org.pac4j.jwt.config.signature.RSASignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.jwt.util.JWKHelper;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.OidcProfile;

import java.io.IOException;
import java.net.URL;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SecurityConfigFactory implements ConfigFactory {
    private final JwtAuthenticator jwtAuthenticator = new JwtAuthenticator();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Config build(final Object... parameters) {
        System.out.print("Building Security configuration...\n");

        final OidcConfiguration oidcConfiguration = new OidcConfiguration();
        oidcConfiguration.setClientId("0oag2m4wn0w19vNaV0h7");
        oidcConfiguration.setSecret("U6f5I3Gkc3WkdoKfnWP8lw0bZtMRifnAtGWUTi0Q");
        oidcConfiguration.setDiscoveryURI("https://dev-737523.oktapreview.com/oauth2/default/.well-known/openid-configuration");
        oidcConfiguration.setUseNonce(true);
        final OidcClient<OidcProfile, OidcConfiguration> oidcClient = new OidcClient<>(oidcConfiguration);
        oidcClient.setAuthorizationGenerator((ctx, profile) -> {
            profile.addRole("ROLE_ADMIN");
            return profile;
        });

        HeaderClient headerClient = new HeaderClient("Authorization", "Bearer ", (credentials, ctx) -> {
            String token = ((TokenCredentials) credentials).getToken();
            if (token != null) {
                try {
                    // Get JWK
                    URL keysUrl = new URL("https://dev-737523.oktapreview.com/oauth2/default/v1/keys");
                    Map map = mapper.readValue(keysUrl, Map.class);
                    List keys = (ArrayList) map.get("keys");
                    String json = mapper.writeValueAsString(keys.get(0));

                    // Build key pair and validate token
                    KeyPair rsaKeyPair = JWKHelper.buildRSAKeyPairFromJwk(json);
                    jwtAuthenticator.addSignatureConfiguration(new RSASignatureConfiguration(rsaKeyPair));
                    CommonProfile profile = jwtAuthenticator.validateToken(token);
                    credentials.setUserProfile(profile);
                    System.out.println("Hello, " + profile.getId());
                } catch (IOException e) {
                    System.err.println("Failed to validate Bearer token: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        final Clients clients = new Clients("http://localhost:8080/callback",
                oidcClient, headerClient, new AnonymousClient());
        return new Config(clients);
    }
}