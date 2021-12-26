package com.tweetapp.backend.models;

import java.util.Date;
import java.util.LinkedList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
@Document
public class Tweet {
    @Id
    private String id;
    private String content;
    private String createdBy;
    private Date createdAt;
    private LinkedList<Like> likes;
    private LinkedList<Reply> replies;
}
