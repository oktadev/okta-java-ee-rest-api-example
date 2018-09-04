package com.okta.developer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.Filter;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Rob Winch
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SecurityConfiguration.class)
public class SecurityConfigurationTests {
	@Autowired
	Filter springSecurityFilterChain;

	MockMvc mockMvc;

	@Before
	public void mockMvc() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(new Controller())
				.apply(springSecurity(this.springSecurityFilterChain))
				.build();
	}

	@Test
	public void indexJspWhenNotAuthenticatedThenRedirectForAuthentication() throws Exception {
		this.mockMvc.perform(get("/index.jsp"))
				.andExpect(loginRequested());
	}

	@Test
	public void beerJsfWhenNotAuthenticatedThenRedirectForAuthentication() throws Exception {
		this.mockMvc.perform(get("/index.jsp"))
				.andExpect(loginRequested());
	}

	private ResultMatcher loginRequested() throws Exception {
		return ResultMatcher.matchAll(status().is3xxRedirection(),
				redirectedUrl("http://localhost/oauth2/authorization/dev-737523.oktapreview.com"));
	}

	@RestController
	static class Controller {}
}
