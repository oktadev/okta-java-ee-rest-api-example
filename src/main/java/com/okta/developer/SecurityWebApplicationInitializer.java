package com.okta.developer;

import org.springframework.security.web.context.*;

public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {

	public SecurityWebApplicationInitializer() {
		super(SecurityConfiguration.class);
	}
}