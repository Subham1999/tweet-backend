package com.tweetapp.backend.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Builder;
import lombok.Data;

public class MyAuthenticationEntrypoint implements AuthenticationEntryPoint {

	private static final int AUTH_ERR = 460;
//    private static final String ERROR_RESPONSE = "Authentication Exception";
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		RootCause root_cause = RootCause.builder().root_cause(ExceptionUtils.getStackTrace(authException))
				.message(ExceptionUtils.getMessage(authException)).build();

		String jsonCause = OBJECT_MAPPER.writeValueAsString(root_cause);

		response.setStatus(AUTH_ERR);
		PrintWriter writer = response.getWriter();
		writer.append(jsonCause);
		writer.close();
		response.flushBuffer();
	}

}

@Data
@Builder
class RootCause {
	public static String getType() {
		return type;
	}

	private static final String type = "Authentication Exception";
	private String root_cause;
	private String message;
}