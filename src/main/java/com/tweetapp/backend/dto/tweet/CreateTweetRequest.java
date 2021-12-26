package com.tweetapp.backend.dto.tweet;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateTweetRequest {
    @NotBlank(message = "Tweet content should not be blank")
    private String content;
    @Email(message = "Valid email id as username will be excepted")
    @NotBlank(message = "Created by or author email address should not be blank")
    private String createdBy;
    private Date createdAt;
}
