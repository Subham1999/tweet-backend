package com.tweetapp.backend.dao.tweet;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tweetapp.backend.models.Tweet;

public interface TweetRepository extends MongoRepository<Tweet, String> {
}
