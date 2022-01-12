package com.tweetapp.backend.dto.tweet;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonInclude(Include.NON_NULL)
public class CreateTweetResponse {
    private String id;
    private String content;
    private String createdBy;
    private Date createdAt;
}
