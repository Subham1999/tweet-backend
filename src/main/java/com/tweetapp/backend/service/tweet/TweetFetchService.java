package com.tweetapp.backend.service.tweet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tweetapp.backend.models.Tweet;

public interface TweetFetchService {

	Page<Tweet> fetchGlobalFeed(Pageable pageRequest);

	Page<Tweet> fetchRecentPostsByAuthor(String authorEmail, Pageable pageRequest);

	Page<Tweet> fetchMostLikedPostsByAuthor(String authorEmail, Pageable pageRequest);

}
