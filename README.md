# Java EE REST API + Security
 
This example app shows how to build a Java EE REST API and secure it with JWT and OIDC.

Please read [Build a Java EE REST API; Secure it with JWT and OIDC]() to see how this app was created.

**Prerequisites:** [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html), [Maven](https://maven.apache.org), and an [Okta Developer Account](https://developer.okta.com).

> [Okta](https://developer.okta.com/) has Authentication and User Management APIs that reduce development time with instant-on, scalable user infrastructure. Okta's intuitive API and expert support make it easy for developers to authenticate, manage, and secure users and roles in any application.

* [Getting Started](#getting-started)
* [Links](#links)
* [Help](#help)
* [License](#license)

## Getting Started

You will need to create an OIDC Application in Okta to get your settings to perform authentication. 

1. Log in to your developer account on [developer.okta.com](https://developer.okta.com).
2. Navigate to **Applications** and click on **Add Application**.
3. Select **Web** and click **Next**. 
4. Give the application a name (e.g., `Java EE Secure API`) and add the following as Login redirect URIs:
    * `http://localhost:3000/implicit/callback`
    * `http://localhost:8080/login/oauth2/code/okta`
    * `http://localhost:8080/callback?client_name=OidcClient`
4. Click **Done**, then edit the project and enable "Implicit (Hybrid)" as a grant type (allow ID and access tokens) and click **Save**.

### JWT Verifier for Java

To see how the JWT Verifier for Java works, clone this project and check out the `jwt-verifier` branch.

```bash
git clone -b jwt-verifier https://github.com/oktadeveloper/okta-java-ee-rest-api-example.git
```

Then modify `src/main/java/com/okta/developer/JwtFilter.java` and replace the issuer and client ID with the values from the app you created.

```java
public void init(FilterConfig filterConfig) {
    try {
        jwtVerifier = new JwtHelper()
                .setIssuerUrl("https://{yourOktaDomain}/oauth2/default")
                .setClientId("{yourClientId}")
                .build();
    } catch (IOException | ParseException e) {
        System.err.print("Configuring JWT Verifier failed!");
        e.printStackTrace();
    }
}
```

Start the app using `mvn clean package tomee:run`. 

To prove it works with a valid JWT, you can clone our Bootiful React project, and run its UI:

```bash
git clone -b okta https://github.com/oktadeveloper/spring-boot-react-example.git bootiful-react
cd bootiful-react/client
npm install
```

Edit this project's `src/App.tsx` file and change the `issuer` and `clientId` to match your application. 

```ts
const config = {
  issuer: 'https://{yourOktaDomain}/oauth2/default',
  redirectUri: window.location.origin + '/implicit/callback',
  clientId: '{yourClientId}'
};
```

Then start it:

```
npm start
```

You should then be able to login at `http://localhost:3000` with the credentials you created your account with.

### Spring Security

The Spring Security implementation in this project will prompt you to login when you try to access the API, and it will setup a resource server that can serve data to a JavaScript client.

To see Spring Security with Java EE in action, clone this project and check out the `spring-security` branch.

```bash
git clone -b spring-security https://github.com/oktadeveloper/okta-java-ee-rest-api-example.git
```

Update `src/main/resources/application.properties` and fill it with your Okta OIDC app settings.

```properties
okta.client-id={clientId}
okta.client-secret={clientSecret}
okta.issuer-uri=https://{yourOktaDomain}/oauth2/default
```

Then start the app using `mvn clean package tomee:run`.

If you try to access `http://localhost:8080`, you'll be redirected to Okta to log in. If you use the aforementioned React client to talk to your API, everything should just work.

## Pac4J

The Pac4J implementation in this project is very similar to Spring Security. It'll prompt you to log in when you hit the API directly, or look for an `Authorization` header if you talk to it from a JavaScript client.

To see Pac4J with Java EE in action, clone this project and check out the `pac4j` branch.

```bash
git clone -b pac4j https://github.com/oktadeveloper/okta-java-ee-rest-api-example.git
```

Update `src/main/java/com/okta/developer/SecurityConfigFactory.java` and change the issuer, client ID, and client secret to match your Okta app.

```java
public class SecurityConfigFactory implements ConfigFactory {
    private final JwtAuthenticator jwtAuthenticator = new JwtAuthenticator();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Config build(final Object... parameters) {
        System.out.print("Building Security configuration...\n");

        final OidcConfiguration oidcConfiguration = new OidcConfiguration();
        oidcConfiguration.setClientId("{yourClientId}");
        oidcConfiguration.setSecret("{yourClientSecret}");
        oidcConfiguration.setDiscoveryURI("https://{yourOktaDomain}/oauth2/default/.well-known/openid-configuration");
        oidcConfiguration.setUseNonce(true);
        final OidcClient<OidcProfile, OidcConfiguration> oidcClient = new OidcClient<>(oidcConfiguration);
        oidcClient.setAuthorizationGenerator((ctx, profile) -> {
            profile.addRole("ROLE_USER");
            return profile;
        });

        HeaderClient headerClient = new HeaderClient("Authorization", "Bearer ", (credentials, ctx) -> {
            String token = ((TokenCredentials) credentials).getToken();
            if (token != null) {
                try {
                    // Get JWK
                    URL keysUrl = new URL("https://{yourOktaDomain}/oauth2/default/v1/keys");
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
```

Start the app using `mvn clean package tomee:run`.

If you try to access `http://localhost:8080`, you'll be redirected to Okta to log in. If you use the aforementioned React client to talk to your API, everything should just work.

## Links

This example uses the following open source libraries:

* [Apache TomEE](http://tomee.apache.org/)
* [JWT Verifier for Java](https://github.com/okta/okta-jwt-verifier-java)
* [Spring Security](https://spring.io/projects/spring-security)
* [Pac4j for J2E](https://github.com/pac4j/j2e-pac4j)

## Help

Please post any questions as comments on the [blog post](), or visit our [Okta Developer Forums](https://devforum.okta.com/). You can also email developers@okta.com if you'd like to create a support ticket.

## License

Apache 2.0, see [LICENSE](LICENSE).