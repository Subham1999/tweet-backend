package com.tweetapp.backend.models;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@Builder
@ToString
@Document(collection = "USER_DETAILS")
public class User {
	private String firstName;
	private String lastName;
	@Id
	private String email;
	private String password;
	private Date dateOfJoin;
	private boolean isSecretShared;
}
