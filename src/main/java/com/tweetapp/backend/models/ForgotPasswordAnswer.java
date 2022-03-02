package com.tweetapp.backend.models;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Document(collection = "USER_SECRETS")
public class ForgotPasswordAnswer {

    @Id
    @Email
    private String userEmail;

    @Pattern(regexp = "[0-9]{8}", message = "Invalid secret key")
    private String answer;
}
