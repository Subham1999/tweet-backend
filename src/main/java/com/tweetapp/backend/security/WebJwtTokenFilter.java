package com.tweetapp.backend.security;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private MyUserDetailService userDetailService;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
	    final FilterChain filterChain) throws ServletException, IOException {

//	request.getHeaderNames().asIterator()
//		.forEachRemaining(hn -> System.out.printf("Header::: %s --> %s\n", hn, request.getHeader(hn)));

	final String authHeader = request.getHeader("Authorization");
	String userName = null;
	String jwtToken = null;

	if (Objects.nonNull(authHeader) && authHeader.startsWith("Bearer ")) {
	    jwtToken = authHeader.substring(7);
	    try {
		userName = jwtTokenUtil.extractUserName(jwtToken);
	    } catch (Throwable throwable) {
		System.out.println("Exception at \"doFilterInternal\" :: " + throwable.getMessage());
//		throw throwable;
	    }
	}

	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	System.out.println("Auth header::" + authHeader);
	System.out.println("Auth userName::" + userName);
	System.out.println("Authentication::" + authentication);

	if (Objects.nonNull(userName) && Objects.isNull(authentication)) {
	    final UserDetails loadUserByUsername = userDetailService.loadUserByUsername(userName);
	    if (jwtTokenUtil.validateToken(jwtToken, loadUserByUsername)) {

		System.out.println("USER Validated-----------------------");
		final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
			jwtToken, null, loadUserByUsername.getAuthorities());
		usernamePasswordAuthenticationToken
			.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
	    } else {
		System.out.println("USER NOT Validated-----------------------");
	    }
	} else {
	    System.out.println("USER NOT Validated because 'null' issue -----------------------");
	}

	filterChain.doFilter(request, response);
    }

}
