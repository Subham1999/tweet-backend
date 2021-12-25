package com.tweetapp.backend.models;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Like {
    private String likerId;
    private Date likedDate;
}
