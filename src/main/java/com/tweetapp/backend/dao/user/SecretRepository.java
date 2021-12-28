package com.tweetapp.backend.dao.user;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tweetapp.backend.models.ForgotPasswordAnswer;

public interface SecretRepository extends MongoRepository<ForgotPasswordAnswer, String> {

}
