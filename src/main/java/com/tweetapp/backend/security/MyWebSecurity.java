package com.tweetapp.backend.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Component;

import com.tweetapp.backend.controller.MyAuthenticationEntrypoint;

@Component
public class MyWebSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyUserDetailService userDetailService;

    @Autowired
    private WebJwtTokenFilter jwtTokenFilter;

    private static final String[] WHITE_LIST = {
	    // TWEET-APP-URLS
	    "/auth/", "/auth/generate_token", "/auth/forgot_password", "/users/health", "/users/register",
	    "/users/secret_share",

	    // SWAGGER-URLS
	    "/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/security", "/swagger-ui.html",
	    "/webjars/**" };

    private static final String[] CRITICAL_PATHS = {
	    // ACTUATOR-URLS
	    "/actuator/**" };

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth.userDetailsService(userDetailService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
	http.authorizeRequests().antMatchers(WHITE_LIST).permitAll();
//	http.authorizeRequests().antMatchers(CRITICAL_PATHS).hasAnyRole("ROLE_ADMIN", "ADMIN");
	http.authorizeRequests().antMatchers(CRITICAL_PATHS).permitAll();
	http.authorizeRequests().anyRequest().authenticated();

	// HTTP Security Customization
	http.cors();
	http.csrf().disable();
	http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
	http.exceptionHandling().authenticationEntryPoint(new MyAuthenticationEntrypoint());
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
	return super.authenticationManagerBean();
    }
}
