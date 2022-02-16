package com.tweetapp.backend.dto.auth;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ForgotPasswordRequest {
	private String email;
	private String secret;
	private String newPassword;
}
