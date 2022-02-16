package com.tweetapp.backend.dto.tweet.like;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class DeleteLikeRequest {
	@NotBlank
	private String postId;

	@NotBlank
	@Email
	private String likedBy;
}
