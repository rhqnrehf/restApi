package io.onshipping.demo.configs;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import org.junit.Test;
import org.mockito.internal.invocation.MockitoMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import io.onshipping.demo.accounts.Account;
import io.onshipping.demo.accounts.AccountRole;
import io.onshipping.demo.accounts.AccountService;
import io.onshipping.demo.common.AppProperties;
import io.onshipping.demo.common.BaseControllerTest;
import io.onshipping.demo.common.TestDescription;

public class AuthServerConfigTest extends BaseControllerTest{
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	AppProperties appProperties;

	
	@Test
	@TestDescription("인증 토큰을 발급받는 테스트")
	public void getAuthToken() throws Exception {
		String username = "keesun@email.com";
		String password = "keesun";
		Account account = Account.builder()
				.email(username)
				.password(password)
				.roles(Set.of(AccountRole.ADMIN,AccountRole.USER))
				.build();
		accountService.saveAccount(account);
		
		mockMvc.perform(post("/oauth/token")
				.with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
				.param("username", username)
				.param("password", password)
                .param("grant_type", "password"))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("access_token").exists());


	}
}
