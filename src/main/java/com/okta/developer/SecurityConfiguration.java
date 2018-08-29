package com.okta.developer;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@PropertySource("classpath:application.properties")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static List<String> clients = Collections.singletonList("okta");
    private final Environment env;

    @Autowired
    public SecurityConfiguration(Environment env) {
        this.env = env;
    }


    /*@Autowired
public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .inMemoryAuthentication()
            .withUser("user").password("{noop}password").roles("USER");
}*/

	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
            /*.oauth2Login()
                .clientRegistrationRepository(clientRegistrationRepository())
                .authorizedClientService(authorizedClientService())
                .and()*/
            .oauth2().resourceServer()
                .jwt().jwkSetUri("https://dev-737523.oktapreview.com/oauth2/default/v1/keys")
                .and()
                .and()
            .requestMatcher(new RequestHeaderRequestMatcher("Authorization"))
            .authorizeRequests()
                .antMatchers("/**/*.{js,html,css}").permitAll()
                .anyRequest().authenticated();
    }

    // Excellent info on oauth2Login(): https://www.baeldung.com/spring-security-5-oauth2-login
    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = clients.stream()
                .map(this::getRegistration)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new InMemoryClientRegistrationRepository(registrations);
    }

    private ClientRegistration getRegistration(String client) {
        String REGISTRATION_KEY = "spring.security.oauth2.client.registration.";
        String clientId = env.getProperty(REGISTRATION_KEY + client + ".client-id");
        String clientSecret = env.getProperty(REGISTRATION_KEY + client + ".client-secret");

        String PROVIDER_KEY = "spring.security.oauth2.client.provider.";
        String authorizationUri = env.getProperty(PROVIDER_KEY + client + ".user-authorization-uri");
        String tokenUri = env.getProperty(PROVIDER_KEY + client + ".access-token-uri");
        String userInfoUri = env.getProperty(PROVIDER_KEY + client + ".user-info-uri");
        String jwkSetUri = env.getProperty(PROVIDER_KEY + client + ".jwk-set-uri");

        if (client.equals("okta")) {
            return CommonOAuth2Provider.OKTA.getBuilder(client)
                    .clientId(clientId).clientSecret(clientSecret)
                    .authorizationUri(authorizationUri)
                    .tokenUri(tokenUri)
                    .userInfoUri(userInfoUri)
                    .jwkSetUri(jwkSetUri).build();
        }
        return null;
    }
}