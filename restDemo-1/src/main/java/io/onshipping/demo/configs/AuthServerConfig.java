package io.onshipping.demo.configs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import io.onshipping.demo.accounts.AccountService;
import io.onshipping.demo.common.AppProperties;

@Configuration
@EnableAuthorizationServer
@Order(SecurityProperties.BASIC_AUTH_ORDER)
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter{
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	AccountService accountService;
	
	@Autowired
	TokenStore tokenStore;
	
	@Autowired
	DataSource dataSource;
	@Autowired
	AppProperties appProperties;

	
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.passwordEncoder(passwordEncoder);
	}
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
			.withClient(appProperties.getClientId())
			.authorizedGrantTypes("password","refresh_token")
			.scopes("read","write")
			.secret(passwordEncoder.encode(appProperties.getClientSecret()))
			.accessTokenValiditySeconds(10*60)
			.refreshTokenValiditySeconds(3600);
	}
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(authenticationManager)
			.userDetailsService(accountService)
			.tokenStore(tokenStore);

	}
	
	
}
