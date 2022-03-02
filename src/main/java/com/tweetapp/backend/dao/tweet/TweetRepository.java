package com.tweetapp.backend.dao.tweet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.tweetapp.backend.models.Tweet;

public interface TweetRepository extends MongoRepository<Tweet, String> {

    @Query(value = "{'createdBy' : ?0}")
    Page<Tweet> findAllOrderByCreatedAtDesc(String createdBy, Pageable pageable);
}
