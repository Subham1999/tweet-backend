package com.tweetapp.backend.service.tweet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tweetapp.backend.dao.tweet.TweetRepository;
import com.tweetapp.backend.models.Tweet;

@Service
public class TweetFetchServiceImpl implements TweetFetchService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TweetFetchServiceImpl.class);

	@Autowired
	private TweetRepository tweetRepository;

//    @Autowired
//    private MongoTemplate mongoTemplate;

	@Override
	public Page<Tweet> fetchGlobalFeed(Pageable pageRequest) {
		LOGGER.info("Inside 'fetchGlobalFeed'...");
		return tweetRepository.findAll(pageRequest);
	}

	@Override
	public Page<Tweet> fetchRecentPostsByAuthor(String authorEmail, Pageable pageRequest) {
		LOGGER.info("Inside 'fetchRecentPostsByAuthor'... User {}", authorEmail);
		return tweetRepository.findAllOrderByCreatedAtDesc(authorEmail, pageRequest);
	}

	@Override
	public Page<Tweet> fetchMostLikedPostsByAuthor(String authorEmail, Pageable pageRequest) {
		LOGGER.info("Inside 'fetchMostLikedPostsByAuthor'... User {}", authorEmail);
		return null;
	}

}
