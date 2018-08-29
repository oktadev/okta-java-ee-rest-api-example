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
4. Give the application a name (.e.g., `Java EE Secure API`) and add the following as Login redirect URIs:
    * `http://localhost:8080/login/oauth2/code/okta	`
    * `http://localhost:8080/callback?client_name=OidcClient`
4. Click **Done**, then edit the project and enable "Implicit (Hybrid)" as a grant type and click **Save**.

<!-- todo: add additional instructions -->

### JWT Verifier for Java

### Spring Security

## Pac4J

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