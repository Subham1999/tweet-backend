package com.tweetapp.backend.dto.user.auth;

import com.tweetapp.backend.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LoginResponse extends Response {
	private String message;
}
