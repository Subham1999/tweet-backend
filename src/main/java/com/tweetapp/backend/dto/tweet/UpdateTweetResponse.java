package com.tweetapp.backend.dto.tweet;

import com.tweetapp.backend.models.Tweet;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateTweetResponse {

	private Tweet tweet;
}
