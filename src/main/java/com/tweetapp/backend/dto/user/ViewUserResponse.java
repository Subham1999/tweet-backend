package com.tweetapp.backend.dto.user;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ViewUserResponse {
    private String _message;
    private int _status_code;

    private String firstName;
    private String lastName;
    private String email;
    private Date dateOfJoin;
}
