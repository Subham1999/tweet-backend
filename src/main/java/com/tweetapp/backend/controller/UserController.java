package com.tweetapp.backend.controller;

import com.tweetapp.backend.dto.user.CreateUserRequest;
import com.tweetapp.backend.dto.user.CreateUserResponse;
import com.tweetapp.backend.dto.user.UpdateUserRequest;
import com.tweetapp.backend.dto.user.UpdateUserResponse;
import com.tweetapp.backend.dto.user.ViewUserResponse;
import com.tweetapp.backend.dto.user.auth.LoginRequest;
import com.tweetapp.backend.dto.user.auth.LoginResponse;

public interface UserController {
    ViewUserResponse viewUser(String email);

    CreateUserResponse createUser(CreateUserRequest createUserRequest);

    UpdateUserResponse updateUser(String email, UpdateUserRequest updateUserRequest);

    LoginResponse attemptLogin(LoginRequest loginRequest);
}
