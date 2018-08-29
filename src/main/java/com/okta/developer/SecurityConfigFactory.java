package com.okta.developer;

import org.pac4j.core.client.Clients;
import org.pac4j.core.client.direct.AnonymousClient;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.http.client.direct.HeaderClient;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.profile.OidcProfile;

public class SecurityConfigFactory implements ConfigFactory {

    @Override
    public Config build(final Object... parameters) {
        System.out.print("Building Security configuration...\n");

        final OidcConfiguration oidcConfiguration = new OidcConfiguration();
        oidcConfiguration.setClientId("0oag2m4wn0w19vNaV0h7");
        oidcConfiguration.setSecret("U6f5I3Gkc3WkdoKfnWP8lw0bZtMRifnAtGWUTi0Q");
        oidcConfiguration.setDiscoveryURI("https://dev-737523.oktapreview.com/oauth2/default/.well-known/openid-configuration");
        oidcConfiguration.setUseNonce(true);
        final OidcClient<OidcProfile, OidcConfiguration> oidcClient = new OidcClient<>(oidcConfiguration);
        oidcClient.setAuthorizationGenerator((ctx, profile) -> { profile.addRole("ROLE_ADMIN"); return profile; });

        final Clients clients = new Clients("http://localhost:8080/callback", oidcClient, new AnonymousClient());
        return new Config(clients);
    }
}