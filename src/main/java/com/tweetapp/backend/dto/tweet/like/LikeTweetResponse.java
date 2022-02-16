package com.tweetapp.backend.dto.tweet.like;

import java.util.Set;

import com.tweetapp.backend.models.Like;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LikeTweetResponse {
	private Set<Like> likes;
}
