package com.tweetapp.backend.service.user;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Provider;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tweetapp.backend.dao.user.UserRepository;
import com.tweetapp.backend.dto.user.CreateUserRequest;
import com.tweetapp.backend.dto.user.CreateUserResponse;
import com.tweetapp.backend.dto.user.UpdateUserRequest;
import com.tweetapp.backend.dto.user.UpdateUserResponse;
import com.tweetapp.backend.dto.user.Users;
import com.tweetapp.backend.dto.user.ViewUserRequest;
import com.tweetapp.backend.dto.user.ViewUserResponse;
import com.tweetapp.backend.dto.user.auth.LoginRequest;
import com.tweetapp.backend.dto.user.auth.LoginResponse;
import com.tweetapp.backend.exceptions.InvalidRequest;
import com.tweetapp.backend.models.User;
import com.tweetapp.backend.service.tweet.HttpRequestDataHolder;

@Service
public class UserServiceImpl implements UserService {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

	private static final int STATUS_FAILED = 0;
	private static final int STATUS_SUCCESS = 1;

	private static final String USER_RECORD_CANNOT_BE_CREATED = "User record cannot be created : ";
	private static final String INVALID_ARGUMENT = "Invalid argument : ";
	private static final String USER_RECORD_CREATED = "User record created";
	private static final String NO_USER_FOUND_WITH_THIS_EMAIL = "No user found with this email : ";
	private static final String PROVIDED_EMAIL_IS_NULL = "Provided email is null";
//    private static final String USER_RECORD_FOUND = "User record found";
	private static final String USER_RECORD_UPDATED = "User record updated";
	private static final String USER_RECORD_NOT_UPDATED = "User record can not be updated";
//    private static final String INVALID_LOGIN_REQUEST = "Invalid login request";
//    private static final String EMAIL_CAN_NOT_BE_EMPTY_OR_NULL = " Email can not be empty or null";
//    private static final String PASSWORD_CAN_NOT_BE_EMPTY_OR_NULL = "Password can not be empty or null ";

	@Autowired
	private Provider<HttpRequestDataHolder> provider;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public ViewUserResponse viewUser(ViewUserRequest viewUserRequest) {
		LOGGER.info("Inside 'viewUser' User {}", viewUserRequest.getMail());
		if (Objects.isNull(viewUserRequest)) {
//	    return ViewUserResponse.builder()._status_code(STATUS_FAILED)._message(INVALID_ARGUMENT + viewUserRequest)
//		    .build();
			throw new InvalidRequest("Request body is empty");
		}
		final String mail = viewUserRequest.getMail();
		if (Objects.isNull(mail)) {
//	    return ViewUserResponse.builder()._status_code(STATUS_FAILED)._message(PROVIDED_EMAIL_IS_NULL).build();
			throw new InvalidRequest("Mail is NULL");
		}

		Optional<User> optionalUser = userRepository.findByEmail(mail);
		if (optionalUser.isPresent()) {
			LOGGER.info("User {} found!!!", viewUserRequest.getMail());
			final User user = optionalUser.get();

			final String tweetURL = "tweets/search?fetchType=recentposts&created_by=" + user.getEmail();
			return ViewUserResponse.builder().dateOfJoin(user.getDateOfJoin()).firstName(user.getFirstName())
					.lastName(user.getLastName()).isSecretShared(user.isSecretShared()).email(user.getEmail())
					._tweet_resourse_uri(tweetURL).build();
		} else {
			LOGGER.error("User {} NOT found!!!", viewUserRequest.getMail());
			throw new InvalidRequest("User NOT found");
		}
	}

	@Override
	public CreateUserResponse createUser(CreateUserRequest createUserRequest) {
		LOGGER.info("Inside 'createUser'");
		// 'null' check
		if (Objects.isNull(createUserRequest)) {
			LOGGER.error("createUserRequest is NULL");
			return CreateUserResponse.builder()._status_code(STATUS_FAILED)
					._message(INVALID_ARGUMENT + createUserRequest).build();
		}

		// Check whether 'email' already exists or not
		final String email = createUserRequest.getEmail();
		if (userExists(email)) {
			LOGGER.error("User {} already exists!", email);
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
			LOGGER.info("User {} saved in DB!", email);
			createUserResponse = CreateUserResponse.builder()._status_code(STATUS_SUCCESS)._message(USER_RECORD_CREATED)
					.firstName(savedUser.getFirstName()).lastName(savedUser.getLastName()).email(savedUser.getEmail())
					.dateOfJoin(savedUser.getDateOfJoin()).build();
		} catch (Exception e) {
			LOGGER.info("User {} can not be saved in DB!::: Exception : {}", email, e.getMessage());
			createUserResponse = CreateUserResponse.builder()._status_code(STATUS_FAILED)
					._message(USER_RECORD_CANNOT_BE_CREATED + ExceptionUtils.getMessage(e)).build();
		}

		return createUserResponse;
	}

	@Override
	public UpdateUserResponse updateUser(final UpdateUserRequest updateUserRequest) {
		LOGGER.info("Inside 'updateUser' User {}", updateUserRequest.getEmail());
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
			LOGGER.info("User {} found in DB!!", email);
			final User user = foundByEmail.get();
			if (Objects.nonNull(updateUserRequest.getFirstName())) {
				if (!StringUtils.equals(updateUserRequest.getFirstName(), user.getFirstName())) {
					LOGGER.info("User {} update for First Name!!", email);
					user.setFirstName(updateUserRequest.getFirstName());
				}
			}

			if (Objects.nonNull(updateUserRequest.getLastName())) {
				if (!StringUtils.equals(updateUserRequest.getLastName(), user.getLastName())) {
					LOGGER.info("User {} update for Last Name!!", email);
					user.setLastName(updateUserRequest.getLastName());
				}
			}

			if (updateUserRequest.isSecretShared()) {
				LOGGER.info("User {} updated because secret is shared", email);
				user.setSecretShared(true);
			}

			try {
				final User updatedUser = userRepository.save(user);
				LOGGER.info("User {} updated!!", email);
				return UpdateUserResponse.builder()._status_code(STATUS_SUCCESS)._message(USER_RECORD_UPDATED)
						.firstName(updatedUser.getFirstName()).lastName(updatedUser.getLastName()).email(email).build();
			} catch (Exception e) {
				LOGGER.error("User {} can not be updated!! Exception : {}", email, e.getMessage());
				return UpdateUserResponse.builder()._status_code(STATUS_FAILED)._message(USER_RECORD_NOT_UPDATED)
						.email(email).build();
			}
		} else { // When 'email' is not present -- cannot update!
			LOGGER.error("User {} NOT found in DB", email);
			return UpdateUserResponse.builder()._status_code(STATUS_FAILED)._message(NO_USER_FOUND_WITH_THIS_EMAIL)
					.email(email).build();
		}
	}

	@Deprecated
	@Override
	public LoginResponse attemptLogin(LoginRequest loginRequest) {
//	if (Objects.isNull(loginRequest)) {
//	    return LoginResponse.builder()._status_code(STATUS_FAILED)._message(INVALID_LOGIN_REQUEST).build();
//	}
//
//	final String encodedPassword = passwordEncoder.encode(loginRequest.getPassword());
//	final String email = loginRequest.getEmail();
//
//	if (Objects.isNull(encodedPassword) || Objects.isNull(email)) {
//	    return LoginResponse.builder()._status_code(STATUS_FAILED)
//		    ._message((Objects.isNull(encodedPassword) ? PASSWORD_CAN_NOT_BE_EMPTY_OR_NULL : "")
//			    + (Objects.isNull(email) ? EMAIL_CAN_NOT_BE_EMPTY_OR_NULL : ""))
//		    .build();
//	} else {
//	    final Optional<User> foundByEmail = userRepository.findByEmail(email);
//
//	    if (foundByEmail.isPresent()) {
//		final User user = foundByEmail.get();
//		final String password = user.getPassword();
//		if (StringUtils.equals(password, encodedPassword)) {
//		    return LoginResponse.builder()._status_code(STATUS_SUCCESS)._message("Credentials are matched")
//			    .build();
//		} else {
//		    return LoginResponse.builder()._status_code(STATUS_FAILED)._message("Credentials are not matched")
//			    .build();
//		}
//	    } else {
//		return LoginResponse.builder()._status_code(STATUS_SUCCESS)._message(NO_USER_FOUND_WITH_THIS_EMAIL)
//			.build();
//	    }
//	}
		return null;
	}

	@Override
	public boolean userExists(String email) {
		return userRepository.existsById(email);
	}

	@Override
	public Users getAllUsers(PageRequest pageRequest) {
		LOGGER.info("Inside 'getAllUsers'...");
		try {
			final Page<User> page = userRepository.findAll(pageRequest);
			final List<ViewUserResponse> user = page.getContent().stream().map(this::mapToView)
					.collect(Collectors.toList());
			boolean first = page.isFirst();
			boolean last = page.isLast();
			boolean empty = page.isEmpty();
			Users users = Users.builder().user(user).isEmpty(empty).isFirst(first).isLast(last).build();
			return users;
		} catch (Exception e) {
			LOGGER.info("############## Exception : {}", ExceptionUtils.getStackTrace(e));
			throw new RuntimeException(e);
		}
	}

	public ViewUserResponse mapToView(User user) {

		return ViewUserResponse.builder()
				._tweet_resourse_uri("tweets/search?fetchType=recentposts&created_by=".concat(user.getEmail()))
				.email(user.getEmail()).dateOfJoin(user.getDateOfJoin()).firstName(user.getFirstName())
				.lastName(user.getLastName()).isSecretShared(user.isSecretShared()).build();
	}

	@Override
	public Users searchUser(final String key) {
		LOGGER.info("Searching User.... KEY : {}", key);
		final List<ViewUserResponse> users = userRepository.findUserByKey(key).stream().map(this::mapToView)
				.collect(Collectors.toList());

		return Users.builder().user(users).isEmpty(users.isEmpty()).isFirst(true).isLast(true).build();
	}
}
