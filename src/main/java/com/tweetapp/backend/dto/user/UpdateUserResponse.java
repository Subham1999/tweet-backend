package com.tweetapp.backend.dto.user;

import com.tweetapp.backend.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UpdateUserResponse extends Response {
	private String firstName;
	private String lastName;
	private String email;
}
