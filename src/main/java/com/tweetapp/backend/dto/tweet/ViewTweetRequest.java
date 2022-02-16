package com.tweetapp.backend.dto.tweet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Data;

/**
 * 
 * @author Subham Santra
 *         <p>
 *         Priority : <code>tweetId > createdBy</code> If tweetId is given then
 *         only single tweet is returned If createdBy is given then tweets
 *         created by the user will be returned
 *         </p>
 *
 */
@JsonInclude(Include.NON_NULL)
@Data
@Builder
public class ViewTweetRequest {
	private String tweetId;
	private String createdBy;
}
