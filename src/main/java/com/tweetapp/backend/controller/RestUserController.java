package com.tweetapp.backend.controller;

import javax.validation.constraints.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.backend.dto.user.CreateUserRequest;
import com.tweetapp.backend.dto.user.CreateUserResponse;
import com.tweetapp.backend.dto.user.UpdateUserRequest;
import com.tweetapp.backend.dto.user.UpdateUserResponse;
import com.tweetapp.backend.dto.user.ViewUserRequest;
import com.tweetapp.backend.dto.user.ViewUserResponse;
import com.tweetapp.backend.dto.user.auth.LoginRequest;
import com.tweetapp.backend.dto.user.auth.LoginResponse;
import com.tweetapp.backend.service.user.UserService;

@RestController
@RequestMapping(path = Urls.USER_BASE)
public class RestUserController implements UserController {

    @Autowired
    private UserService userService;

    private static final String SERVER_CONDITION_GOOD = "SERVER IS UP AND RUNNING";

    @RequestMapping(path = Urls.HEALTH_CHECK, method = RequestMethod.GET)
    public HealthCheckResponse healthCheck() {
	return HealthCheckResponse.builder().serverStatus(SERVER_CONDITION_GOOD).build();
    }

    @RequestMapping(path = "/search/{email}", method = RequestMethod.GET)
    @Override
    public ViewUserResponse viewUser(@PathVariable @Email String email) {
	return userService.viewUser(ViewUserRequest.builder().mail(email).build());
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    @Override
    public CreateUserResponse createUser(@RequestBody CreateUserRequest createUserRequest) {
	return userService.createUser(createUserRequest);
    }

    @RequestMapping(path = "/{email}", method = RequestMethod.PUT)
    @Override
    public UpdateUserResponse updateUser(@PathVariable String email, @RequestBody UpdateUserRequest updateUserRequest) {
	updateUserRequest.setEmail(email);
	return userService.updateUser(updateUserRequest);
    }

    @Override
    public LoginResponse attemptLogin(@RequestBody LoginRequest loginRequest) {
	return userService.attemptLogin(loginRequest);
    }

}
