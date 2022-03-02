package com.tweetapp.backend.dto.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthRequest {

    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
