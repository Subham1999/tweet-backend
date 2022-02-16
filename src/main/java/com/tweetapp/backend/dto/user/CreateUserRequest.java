package com.tweetapp.backend.dto.user;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@Builder
@ToString
public class CreateUserRequest {
	@NotBlank(message = "First name can not be blank")
	private String firstName;
	private String lastName;
	@NotBlank
	@Email(message = "Invalid email")
	private String email;
	@NotBlank
	private String password;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date dateOfJoin;
}
