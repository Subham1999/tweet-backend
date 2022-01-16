package com.tweetapp.backend.models;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
@Document(collection = "TWEET_DETAILS")
public class Tweet {
    @Id
    private String id;
    private String content;
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updatedAt;
    private LinkedHashSet<Like> likes;
    private LinkedList<Reply> replies;
}
