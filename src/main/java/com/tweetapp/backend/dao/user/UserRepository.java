package com.tweetapp.backend.dao.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.tweetapp.backend.models.User;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    @Query(value = "{ $or: [ { 'firstName' : /?0/ }, { 'lastName' : /?0/ }, { 'email' : /?0/ } ] }")
    List<User> findUserByKey(String key);
}
