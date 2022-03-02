package com.tweetapp.backend.dto.user;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Users {
    boolean isFirst;
    boolean isLast;
    boolean isEmpty;
    List<ViewUserResponse> user;
}
