package com.tweetapp.backend.service.user;

import org.springframework.data.domain.PageRequest;

import com.tweetapp.backend.dto.user.CreateUserRequest;
import com.tweetapp.backend.dto.user.CreateUserResponse;
import com.tweetapp.backend.dto.user.UpdateUserRequest;
import com.tweetapp.backend.dto.user.UpdateUserResponse;
import com.tweetapp.backend.dto.user.Users;
import com.tweetapp.backend.dto.user.ViewUserRequest;
import com.tweetapp.backend.dto.user.ViewUserResponse;
import com.tweetapp.backend.dto.user.auth.LoginRequest;
import com.tweetapp.backend.dto.user.auth.LoginResponse;

public interface UserService {

    ViewUserResponse viewUser(ViewUserRequest viewUserRequest);

    CreateUserResponse createUser(CreateUserRequest createUserRequest);

    UpdateUserResponse updateUser(UpdateUserRequest updateUserRequest);

    LoginResponse attemptLogin(LoginRequest loginRequest);

    boolean userExists(String email);

    Users getAllUsers(PageRequest pageRequest);

    Users searchUser(String key);
}
