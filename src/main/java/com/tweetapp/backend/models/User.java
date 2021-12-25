package com.tweetapp.backend.models;

import java.util.Date;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@Builder
@ToString
public class User {
    private String firstName;
    private String lastName;
    @Id
    private String email;
    private String password;
    private Date dateOfJoin;
}
