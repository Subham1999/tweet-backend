package com.tweetapp.backend.dto.user;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CreateUserResponse {
    private int _status_code;
    private String _message;
    
    private String firstName;
    private String lastName;
    private String email;
    private Date dateOfJoin;
}
