package com.tweetapp.backend.security;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.NoArgsConstructor;

/**
 * @author <b>Subham Santra</b>
 *
 */
@Component
@NoArgsConstructor
public class WebJwtTokenFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebJwtTokenFilter.class);

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private MyUserDetailService userDetailService;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
	    final FilterChain filterChain) throws ServletException, IOException {

	final String authHeader = request.getHeader("Authorization");
	String userName = null;
	String jwtToken = null;

	if (Objects.nonNull(authHeader) && authHeader.startsWith("Bearer ")) {
	    jwtToken = authHeader.substring(7);
	    try {
		userName = jwtTokenUtil.extractUserName(jwtToken);
	    } catch (Throwable throwable) {
		LOGGER.error("Exception at 'doFilterInternal()' :: {}", throwable.getMessage());
	    }
	}

	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	LOGGER.info("Auth header::{}, userName::{}", authHeader, userName);

	if (Objects.nonNull(userName) && Objects.isNull(authentication)) {
	    final UserDetails loadUserByUsername = userDetailService.loadUserByUsername(userName);
	    if (jwtTokenUtil.validateToken(jwtToken, loadUserByUsername)) {

		LOGGER.info("USER Validated-----------------------");
		final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
			jwtToken, null, loadUserByUsername.getAuthorities());
		usernamePasswordAuthenticationToken
			.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
	    } else {
		LOGGER.info("USER NOT Validated-----------------------");
	    }
	} else {
	    LOGGER.info("USER NOT Validated because 'null' issue -----------------------");
	}

	filterChain.doFilter(request, response);
    }

}
