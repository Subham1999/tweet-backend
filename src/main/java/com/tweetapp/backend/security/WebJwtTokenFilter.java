package com.tweetapp.backend.security;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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

    private static final String MDC_TOKEN = "req_id";
    private static final String RESPONSE_HEADER = "X-tweet-app-res-ID";
    private static final String REQUEST_HEADER = "X-tweet-app-req-ID";

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
	    final FilterChain filterChain) throws ServletException, IOException {

	try {
	    // ------------------------------------------------------------------------------------------
	    // ------------------------------------------------------------------------------------------
	    final String token;
	    if (!StringUtils.isEmpty(REQUEST_HEADER) && !StringUtils.isEmpty(request.getHeader(REQUEST_HEADER))) {
		token = request.getHeader(REQUEST_HEADER);
	    } else {
		token = Long.toHexString(System.currentTimeMillis()).concat(":")
			.concat(UUID.randomUUID().toString().toUpperCase().replace("-", ""));
	    }

	    MDC.put(MDC_TOKEN, token);
	    if (!StringUtils.isEmpty(RESPONSE_HEADER)) {
		response.addHeader(RESPONSE_HEADER, token);
	    }
	    // ------------------------------------------------------------------------------
	    // ------------------------------------------------------------------------------

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
	    LOGGER.info("request : {}, method : {}, Request header 'Authorization' : {}, user-email : {}", request.getRequestURI(),
		    request.getMethod(), (isHeaderPresent(authHeader) ? "'present'" : "'NOT present'"),
		    userName);
	    if (Objects.nonNull(userName) && Objects.isNull(authentication)) {
		final UserDetails loadUserByUsername = userDetailService.loadUserByUsername(userName);
		if (jwtTokenUtil.validateToken(jwtToken, loadUserByUsername)) {
		    LOGGER.info("#################### USER Validated-----------------------");
		    final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
			    jwtToken, null, loadUserByUsername.getAuthorities());
		    usernamePasswordAuthenticationToken
			    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
		} else {
		    LOGGER.info("#################### USER NOT Validated-----------------------");
		}
	    } else {
		final String issue = isHeaderPresent(authHeader) ? "JWT Expired Or JWT Parse error"
			: "Internal Server error";
		LOGGER.error("#################### USER NOT Validated because {}", issue);
	    }
	    filterChain.doFilter(request, response);
	} catch (Exception e) {
	    LOGGER.error("Exception at 'WebJwtTokenFilter' : {}", ExceptionUtils.getStackTrace(e));
	} finally {
	    MDC.remove(MDC_TOKEN);
	}
    }

    private boolean isHeaderPresent(final String header) {
	return !StringUtils.isEmpty(header);
    }

}