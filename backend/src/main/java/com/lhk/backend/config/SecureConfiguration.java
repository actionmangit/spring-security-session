package com.lhk.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * SecureConfiguration
 */
@Configuration
@EnableWebSecurity
public class SecureConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.httpBasic().and()
			.logout().and()
			.authorizeRequests()
				.antMatchers("/index.html", "/", "/home", "/login").permitAll()
				.anyRequest().authenticated().and()
			.csrf()
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
	}

	@Override
	public void configure(WebSecurity web) {
        web
           .ignoring()
               .antMatchers("/*es2015.*");
    }
}