package com.tweetapp.backend.dto.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class AuthRequest {

    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
