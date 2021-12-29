package com.tweetapp.backend.dto.tweet.reply;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mongodb.lang.NonNull;

import lombok.Data;

@Data
public class ReplyTweetRequest {
    
    @NotBlank
    private String postId;
    
    @NotBlank
    @Length(max = 150, min = 1)
    private String reply;
    
    @Email
    private String repliedBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NonNull
    private Date repliedDate;
}
