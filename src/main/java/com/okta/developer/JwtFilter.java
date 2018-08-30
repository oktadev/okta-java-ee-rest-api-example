package com.okta.developer;

import com.nimbusds.oauth2.sdk.ParseException;
import com.okta.jwt.JoseException;
import com.okta.jwt.Jwt;
import com.okta.jwt.JwtHelper;
import com.okta.jwt.JwtVerifier;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "jwtFilter")
public class JwtFilter implements Filter {
    private JwtVerifier jwtVerifier;

    @Override
    public void init(FilterConfig filterConfig) {
        try {
            jwtVerifier = new JwtHelper()
                    .setIssuerUrl("https://dev-737523.oktapreview.com/oauth2/default")
                    .setClientId("0oafrupkwupwjR8yO0h7")
                    .build();
        } catch (IOException | ParseException e) {
            System.err.print("Configuring JWT Verifier failed!");
            e.printStackTrace();
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        System.out.println("In JwtFilter, path: " + request.getRequestURI());

        // Get access token from authorization header
        String authHeader = request.getHeader("authorization");
        if (authHeader == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access denied.");
            return;
        } else {
            String accessToken = authHeader.substring(authHeader.indexOf("Bearer ") + 7);
            try {
                Jwt jwt = jwtVerifier.decodeAccessToken(accessToken);
                System.out.println("Hello, " + jwt.getClaims().get("sub"));
            } catch (JoseException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access denied.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
} 