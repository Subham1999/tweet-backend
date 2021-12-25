package com.tweetapp.backend.dao.user;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tweetapp.backend.models.User;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);
}
