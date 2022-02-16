package com.tweetapp.backend.dto.tweet;

import lombok.Data;

@Data
public class UpdateTweetRequest {
	private String id;
	private String new_content;
}
