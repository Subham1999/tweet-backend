package com.tweetapp.backend.dto.tweet;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.tweetapp.backend.dto.Response;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@JsonInclude(Include.NON_NULL)
public class CreateTweetResponse extends Response {
    private String id;
    private String content;
    private String createdBy;
    private Date createdAt;
}
