package com.tweetapp.backend.service.tweet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tweetapp.backend.dao.tweet.TweetRepository;
import com.tweetapp.backend.models.Tweet;

@Service
public class TweetFetchServiceImpl implements TweetFetchService {

    @Autowired
    private TweetRepository tweetRepository;

//    @Autowired
//    private MongoTemplate mongoTemplate;

    @Override
    public Page<Tweet> fetchGlobalFeed(Pageable pageRequest) {
	return tweetRepository.findAll(pageRequest);
    }

    @Override
    public Page<Tweet> fetchRecentPostsByAuthor(String authorEmail, Pageable pageRequest) {
	System.out.println("fetchRecentPostsByAuthor : " + authorEmail);
	return tweetRepository.findAllOrderByCreatedAtDesc(authorEmail, pageRequest);
    }

    @Override
    public Page<Tweet> fetchMostLikedPostsByAuthor(String authorEmail, Pageable pageRequest) {
	return null;
    }

}
