package com.tweetapp.backend.dto.tweet.like;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LikeTweetRequest {
    @NotBlank
    private String postId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date likedDate;
    @NotBlank
    private String likerId;
}
