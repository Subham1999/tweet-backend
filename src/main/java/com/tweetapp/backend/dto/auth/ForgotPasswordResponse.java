package com.tweetapp.backend.dto.auth;

import lombok.Data;

@Data
public class ForgotPasswordResponse {
	private String email;
	private String token;
}
