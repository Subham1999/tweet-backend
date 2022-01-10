package com.tweetapp.backend.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class MyAuthenticationEntrypoint implements AuthenticationEntryPoint {

    private static final int auth_err_status = 453;
    private final static String ERROR_RESPONSE = "Authentication Exception";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
	    AuthenticationException authException) throws IOException, ServletException {
	response.setStatus(auth_err_status);
	PrintWriter printWriter = response.getWriter();
	printWriter.append(ERROR_RESPONSE).append("\n").append("ROOT CAUSE::" + authException.getMessage());
	printWriter.close();
	response.flushBuffer();
    }

}
