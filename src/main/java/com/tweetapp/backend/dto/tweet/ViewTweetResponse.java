package com.tweetapp.backend.dto.tweet;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.tweetapp.backend.dto.Response;
import com.tweetapp.backend.models.Tweet;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@JsonInclude(Include.NON_NULL)
@SuperBuilder
@Data
public class ViewTweetResponse extends Response {

	private Tweet tweet;
	private Page<Tweet> tweets;
}
