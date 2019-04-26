package io.onshipping.demo.accounts;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService {

	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	public Account saveAccount(Account account) {
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		accountRepository.save(account);
		return account;
	};
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		Account account = accountRepository.findByEmail(username)
				.orElseThrow(()->new UsernameNotFoundException(username));
		
		
		return new User(account.getEmail(),account.getPassword(),authorities(account.getRoles()));
	}
	private Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
		return roles.stream()
				.map( r -> new SimpleGrantedAuthority("ROLE "+r.name()))
				.collect(Collectors.toSet());
	}


}
