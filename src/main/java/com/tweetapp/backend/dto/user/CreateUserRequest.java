package com.tweetapp.backend.dto.user;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@Builder
@ToString
public class CreateUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Date dateOfJoin;
}
