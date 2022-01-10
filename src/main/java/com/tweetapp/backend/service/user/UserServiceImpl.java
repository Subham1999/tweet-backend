package com.tweetapp.backend.service.user;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tweetapp.backend.dao.user.UserRepository;
import com.tweetapp.backend.dto.user.CreateUserRequest;
import com.tweetapp.backend.dto.user.CreateUserResponse;
import com.tweetapp.backend.dto.user.UpdateUserRequest;
import com.tweetapp.backend.dto.user.UpdateUserResponse;
import com.tweetapp.backend.dto.user.ViewUserRequest;
import com.tweetapp.backend.dto.user.ViewUserResponse;
import com.tweetapp.backend.dto.user.auth.LoginRequest;
import com.tweetapp.backend.dto.user.auth.LoginResponse;
import com.tweetapp.backend.models.User;

@Service
public class UserServiceImpl implements UserService {

    private static final int STATUS_FAILED = 0;
    private static final int STATUS_SUCCESS = 1;

    private static final String USER_RECORD_CANNOT_BE_CREATED = "User record cannot be created : ";
    private static final String INVALID_ARGUMENT = "Invalid argument : ";
    private static final String USER_RECORD_CREATED = "User record created";
    private static final String NO_USER_FOUND_WITH_THIS_EMAIL = "No user found with this email : ";
    private static final String PROVIDED_EMAIL_IS_NULL = "Provided email is null";
    private static final String USER_RECORD_FOUND = "User record found";
    private static final String USER_RECORD_UPDATED = "User record updated";
    private static final String USER_RECORD_NOT_UPDATED = "User record can not be updated";
    private static final String INVALID_LOGIN_REQUEST = "Invalid login request";
    private static final String EMAIL_CAN_NOT_BE_EMPTY_OR_NULL = " Email can not be empty or null";
    private static final String PASSWORD_CAN_NOT_BE_EMPTY_OR_NULL = "Password can not be empty or null ";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ViewUserResponse viewUser(ViewUserRequest viewUserRequest) {
	if (Objects.isNull(viewUserRequest)) {
	    return ViewUserResponse.builder()._status_code(STATUS_FAILED)._message(INVALID_ARGUMENT + viewUserRequest)
		    .build();
	}

	final String mail = viewUserRequest.getMail();
	if (Objects.isNull(mail)) {
	    return ViewUserResponse.builder()._status_code(STATUS_FAILED)._message(PROVIDED_EMAIL_IS_NULL).build();
	}

	Optional<User> optionalUser = userRepository.findByEmail(mail);

	if (optionalUser.isPresent()) {
	    final User user = optionalUser.get();
	    return ViewUserResponse.builder()._status_code(STATUS_SUCCESS)._message(USER_RECORD_FOUND)
		    .dateOfJoin(user.getDateOfJoin()).firstName(user.getFirstName()).lastName(user.getLastName())
		    .isSecretShared(user.isSecretShared()).email(user.getEmail()).build();
	} else {
	    return ViewUserResponse.builder()._status_code(STATUS_FAILED)._message(NO_USER_FOUND_WITH_THIS_EMAIL + mail)
		    .build();
	}
    }

    @Override
    public CreateUserResponse createUser(CreateUserRequest createUserRequest) {
	// 'null' check
	if (Objects.isNull(createUserRequest)) {
	    return CreateUserResponse.builder()._status_code(STATUS_FAILED)
		    ._message(INVALID_ARGUMENT + createUserRequest).build();
	}

	// Check whether 'email' already exists or not
	final String email = createUserRequest.getEmail();
	if (userExists(email)) {
	    return CreateUserResponse.builder()._status_code(STATUS_FAILED)
		    ._message(USER_RECORD_CANNOT_BE_CREATED + " email already exists!").build();
	}

	// Create a user
	final Date CURRENT_DATE = new Date();
	String password = passwordEncoder.encode(createUserRequest.getPassword());
//	String password = createUserRequest.getPassword();
	final User user = User.builder()
		.dateOfJoin(
			(createUserRequest.getDateOfJoin() == null) ? CURRENT_DATE : createUserRequest.getDateOfJoin())
		.firstName(createUserRequest.getFirstName()).lastName(createUserRequest.getLastName())
		.email(createUserRequest.getEmail()).password(password).build();

	CreateUserResponse createUserResponse;

	try {
	    final User savedUser = userRepository.save(user);
	    createUserResponse = CreateUserResponse.builder()._status_code(STATUS_SUCCESS)._message(USER_RECORD_CREATED)
		    .firstName(savedUser.getFirstName()).lastName(savedUser.getLastName()).email(savedUser.getEmail())
		    .dateOfJoin(savedUser.getDateOfJoin()).build();
	} catch (Exception e) {
	    createUserResponse = CreateUserResponse.builder()._status_code(STATUS_FAILED)
		    ._message(USER_RECORD_CANNOT_BE_CREATED + ExceptionUtils.getMessage(e)).build();
	}

	return createUserResponse;
    }

    @Override
    public UpdateUserResponse updateUser(final UpdateUserRequest updateUserRequest) {
	if (Objects.isNull(updateUserRequest)) {
	    return UpdateUserResponse.builder()._status_code(STATUS_FAILED)
		    ._message(INVALID_ARGUMENT + updateUserRequest).build();
	}

	final String email = updateUserRequest.getEmail();

	if (Objects.isNull(email)) {
	    return UpdateUserResponse.builder()._status_code(STATUS_FAILED)._message(PROVIDED_EMAIL_IS_NULL).build();
	}

	final Optional<User> foundByEmail = userRepository.findByEmail(email);

	if (foundByEmail.isPresent()) { // When 'email' present -- then can update
	    final User user = foundByEmail.get();

	    if (Objects.nonNull(updateUserRequest.getFirstName())) {
		if (!StringUtils.equals(updateUserRequest.getFirstName(), user.getFirstName())) {
		    user.setFirstName(updateUserRequest.getFirstName());
		}
	    }

	    if (Objects.nonNull(updateUserRequest.getLastName())) {
		if (!StringUtils.equals(updateUserRequest.getLastName(), user.getLastName())) {
		    user.setLastName(updateUserRequest.getLastName());
		}
	    }

	    try {
		final User updatedUser = userRepository.save(user);

		return UpdateUserResponse.builder()._status_code(STATUS_SUCCESS)._message(USER_RECORD_UPDATED)
			.firstName(updatedUser.getFirstName()).lastName(updatedUser.getLastName()).email(email).build();
	    } catch (Exception e) {
		return UpdateUserResponse.builder()._status_code(STATUS_FAILED)._message(USER_RECORD_NOT_UPDATED)
			.email(email).build();
	    }
	} else { // When 'email' is not present -- cannot update!
	    return UpdateUserResponse.builder()._status_code(STATUS_FAILED)._message(NO_USER_FOUND_WITH_THIS_EMAIL)
		    .email(email).build();
	}
    }

    @Override
    public LoginResponse attemptLogin(LoginRequest loginRequest) {
	if (Objects.isNull(loginRequest)) {
	    return LoginResponse.builder()._status_code(STATUS_FAILED)._message(INVALID_LOGIN_REQUEST).build();
	}

	final String encodedPassword = passwordEncoder.encode(loginRequest.getPassword());
	final String email = loginRequest.getEmail();

	if (Objects.isNull(encodedPassword) || Objects.isNull(email)) {
	    return LoginResponse.builder()._status_code(STATUS_FAILED)
		    ._message((Objects.isNull(encodedPassword) ? PASSWORD_CAN_NOT_BE_EMPTY_OR_NULL : "")
			    + (Objects.isNull(email) ? EMAIL_CAN_NOT_BE_EMPTY_OR_NULL : ""))
		    .build();
	} else {
	    final Optional<User> foundByEmail = userRepository.findByEmail(email);

	    if (foundByEmail.isPresent()) {
		final User user = foundByEmail.get();
		final String password = user.getPassword();
		if (StringUtils.equals(password, encodedPassword)) {
		    return LoginResponse.builder()._status_code(STATUS_SUCCESS)._message("Credentials are matched")
			    .build();
		} else {
		    return LoginResponse.builder()._status_code(STATUS_FAILED)._message("Credentials are not matched")
			    .build();
		}
	    } else {
		return LoginResponse.builder()._status_code(STATUS_SUCCESS)._message(NO_USER_FOUND_WITH_THIS_EMAIL)
			.build();
	    }
	}
    }

    @Override
    public boolean userExists(@NotNull String email) {
	return userRepository.existsById(email);
    }

}
