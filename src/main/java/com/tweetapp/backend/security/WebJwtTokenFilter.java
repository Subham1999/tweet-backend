package com.tweetapp.backend.security;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import javax.inject.Provider;
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

import com.tweetapp.backend.service.tweet.HttpRequestDataHolder;

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

    private static final String REQUESTID = "REQUESTID";
    private static final String MDC_TOKEN = "SESSIONID";
    private static final String RESPONSE_HEADER = "x-tweet-app-res-ID";
    private static final String REQUEST_HEADER = "x-tweet-app-req-ID";

    @Autowired
    private Provider<HttpRequestDataHolder> provider;

    @Autowired
    private UniqueRequestIDGenerator requestIDGenerator;

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
		token = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
	    }

//			System.out.println(token);

	    MDC.put(MDC_TOKEN, token);
	    MDC.put(REQUESTID, requestIDGenerator.get());

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
	    LOGGER.info("****** request={} authorization={} user-email={} client={}",
		    (request.getMethod() + " " + request.getRequestURI()),
		    (isHeaderPresent(authHeader) ? "'present'" : "'NOT present'"), userName,
		    request.getHeader("User-Agent"));
	    if (Objects.nonNull(userName) && Objects.isNull(authentication)) {
		final UserDetails loadUserByUsername = userDetailService.loadUserByUsername(userName);
		if (jwtTokenUtil.validateToken(jwtToken, loadUserByUsername)) {
		    LOGGER.info("!!!!!! User authenticated");
		    final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
			    jwtToken, null, loadUserByUsername.getAuthorities());
		    usernamePasswordAuthenticationToken
			    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
		    provider.get().setUser(userName);
		} else {
		    LOGGER.error("!!!!!! User can not be authenticated **'jwt token is invalid'");
		}
	    } else {
		final String issue = isHeaderPresent(authHeader) ? "JWT Expired Or JWT Parse error"
			: "Internal Server error";
		LOGGER.error("!!!!!! USER NOT Validated because {}", issue);
	    }
	    response.setHeader("Access-Control-Expose-Headers", RESPONSE_HEADER);
	    filterChain.doFilter(request, response);
	} catch (Exception e) {
	    LOGGER.error("Exception at 'WebJwtTokenFilter' : {}", ExceptionUtils.getStackTrace(e));
	} finally {
	    MDC.remove(MDC_TOKEN);
	    MDC.remove(REQUESTID);
	}
    }

    private boolean isHeaderPresent(final String header) {
	if (Objects.nonNull(header) && header.startsWith("Bearer ")) {
	    return header.substring(7).length() > 0;
	}
	return false;
    }

}
