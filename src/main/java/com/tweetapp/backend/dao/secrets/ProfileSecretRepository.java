package com.tweetapp.backend.dao.secrets;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tweetapp.backend.models.ForgotPasswordAnswer;

public interface ProfileSecretRepository extends MongoRepository<ForgotPasswordAnswer, String> {

}
