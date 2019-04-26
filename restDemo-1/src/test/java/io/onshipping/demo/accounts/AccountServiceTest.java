package io.onshipping.demo.accounts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import io.onshipping.demo.common.RestDocsConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Import(RestDocsConfiguration.class)
public class AccountServiceTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	
	@Autowired
	AccountService accountService;

	@Autowired
	PasswordEncoder passwordEncoder;
	@Test
	public void findByUsername() {
		
		String username="kessun@email.com";
		String password="kessun";
		Account account = Account.builder()
				.email(username)
				.password(password)
				.roles(Set.of(AccountRole.ADMIN,AccountRole.USER))
				.build();
		accountService.saveAccount(account);
		UserDetailsService userDetailsService = (UserDetailsService)accountService;
		UserDetails userDetails=userDetailsService.loadUserByUsername(username);
		
		assertThat(this.passwordEncoder.matches(password, userDetails.getPassword())).isTrue();
	}
	
	@Test
	public void findByUsernameFail() {
		String username="random@email.com";
		expectedException.expect(UsernameNotFoundException.class);
		expectedException.expectMessage(Matchers.containsString(username));

		accountService.loadUserByUsername(username);

	}
	
}
