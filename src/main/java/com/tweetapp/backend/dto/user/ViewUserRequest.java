package com.tweetapp.backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ViewUserRequest {
    private String mail;
}
