package com.tweetapp.backend.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;
import javax.validation.constraints.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.backend.commons.MyPasswordEncoder;
import com.tweetapp.backend.dao.user.SecretRepository;
import com.tweetapp.backend.dto.user.CreateUserRequest;
import com.tweetapp.backend.dto.user.CreateUserResponse;
import com.tweetapp.backend.dto.user.UpdateUserRequest;
import com.tweetapp.backend.dto.user.UpdateUserResponse;
import com.tweetapp.backend.dto.user.ViewUserRequest;
import com.tweetapp.backend.dto.user.ViewUserResponse;
import com.tweetapp.backend.models.ForgotPasswordAnswer;
import com.tweetapp.backend.service.user.UserService;

@RestController
@RequestMapping(path = Urls.USER_BASE)
public class RestUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MyPasswordEncoder myPasswordEncoder;

    @Autowired
    private SecretRepository secretRepository;

    private static final String SERVER_CONDITION_GOOD = "SERVER IS UP AND RUNNING";
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
	    Pattern.CASE_INSENSITIVE);

    private boolean isValidEmail(String emailStr) {
	Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
	return matcher.find();
    }

    @RequestMapping(path = Urls.HEALTH_CHECK, method = RequestMethod.GET)
    public HealthCheckResponse healthCheck() {
	return HealthCheckResponse.builder().serverStatus(SERVER_CONDITION_GOOD).build();
    }

    @RequestMapping(path = "/search/{email}", method = RequestMethod.GET)
    public ViewUserResponse viewUser(@PathVariable @Email String email) {
	return userService.viewUser(ViewUserRequest.builder().mail(email).build());
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest createUserRequest) {
	CreateUserResponse createUserResponse = userService.createUser(createUserRequest);
	if (createUserResponse.get_status_code() == 0) {
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createUserResponse);
	} else {
	    return ResponseEntity.status(HttpStatus.OK).body(createUserResponse);
	}
    }

    @RequestMapping(path = "/{email}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateUser(@PathVariable(required = true) String email,
	    @RequestBody UpdateUserRequest updateUserRequest) {

	if (!isValidEmail(email)) {
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email id provided : " + email);
	}

	updateUserRequest.setEmail(email);

	UpdateUserResponse updateUser = userService.updateUser(updateUserRequest);
	if (updateUser.get_status_code() == 0) {
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(updateUser);
	} else {
	    return ResponseEntity.status(HttpStatus.OK).body(updateUser);
	}
    }

//	--- COMENTED OUT BECAUSE A SEPARATE AUTHENTICATION MICROSERVICE CAN BE CREATED
//    public LoginResponse attemptLogin(@RequestBody @Valid LoginRequest loginRequest) {
//	return userService.attemptLogin(loginRequest);
//    }

    @PostMapping("/secret_share")
    public ResponseEntity<?> storeSecrets(@RequestBody @Valid ForgotPasswordAnswer forgotPasswordAnswer) {

	String userEmail = forgotPasswordAnswer.getUserEmail();

	if (userService.userExists(userEmail)) {
	    try {
		String answer = forgotPasswordAnswer.getAnswer();
		answer = myPasswordEncoder.encode(answer);
		forgotPasswordAnswer.setAnswer(answer);
		secretRepository.save(forgotPasswordAnswer);
		return ResponseEntity.ok("Secret stored");
	    } catch (Exception e) {
		return ResponseEntity.internalServerError().body(e.getMessage());
	    }
	} else {
	    return ResponseEntity.badRequest().body("user email " + userEmail + " is not associated with any user");
	}
    }

}
