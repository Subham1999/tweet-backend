package com.tweetapp.backend.service.tweet;

import java.util.Map;

import org.springframework.data.domain.Pageable;

public interface TweetViewConfig {

	TweetFetchType fetchType();

	Map<TweetViewConfigConstant, Object> getConfigMap();

	Pageable getPageRequest();
}
