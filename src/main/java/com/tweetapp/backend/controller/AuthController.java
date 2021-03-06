package com.tweetapp.backend.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.backend.dao.secrets.ProfileSecretRepository;
import com.tweetapp.backend.dao.user.UserRepository;
import com.tweetapp.backend.dto.auth.AuthRequest;
import com.tweetapp.backend.dto.auth.AuthResponse;
import com.tweetapp.backend.dto.auth.ForgotPasswordRequest;
import com.tweetapp.backend.dto.auth.ForgotPasswordResponse;
import com.tweetapp.backend.exceptions.InvalidRequest;
import com.tweetapp.backend.exceptions.PasswordMismatchException;
import com.tweetapp.backend.models.ForgotPasswordAnswer;
import com.tweetapp.backend.security.JwtTokenUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(path = "/auth")
@CrossOrigin
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    @Value("${admin.email}")
    public String adminEmail;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileSecretRepository secretRepository;

    @PostMapping("/generate_token")
    @ApiOperation(value = "Generates a jwt token", notes = "Generates jwt token with help of provided 'email' & 'password' in request body", response = AuthResponse.class, produces = "application/json", consumes = "application/json")
    public AuthResponse generateToken(
	    @ApiParam(value = "Authentication Request META DATA", required = true) @RequestBody AuthRequest authReq) {

	String password = authReq.getPassword();
	String email = authReq.getEmail();

	Optional<com.tweetapp.backend.models.User> optional = userRepository.findByEmail(email);

	if (optional.isPresent()) {
	    LOGGER.info("User found for email : {}", email);
	    String existingPassword = optional.get().getPassword();
	    if (passwordEncoder.matches(password, existingPassword)) {
		LOGGER.info("User password matched");
		String jwtToken = jwtTokenUtil.generateToken(createTmpUser(authReq.getEmail(), authReq.getPassword()));
		return AuthResponse.builder().jwtToken(jwtToken).userEmail(email).build();
	    } else {
		LOGGER.error("User password NOT matched");
		throw new PasswordMismatchException("Password in db is not matching");
	    }
	} else {
	    LOGGER.error("User email: {} is not registered..", email);
	    throw new InvalidRequest("User email is not a registered email");
	}

    }

    private UserDetails createTmpUser(final String email, final String password) {
	final List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));
	return User.withUsername(email).password(password).authorities(authorities).build();
    }

    @PostMapping("/forgot_password")
    @ApiOperation(value = "When user forgot password", notes = "Generates jwt token, when user "
	    + "forgot his/her password but need to provide formatted secret along with new password, password will be updated and user will get jwt token", response = ForgotPasswordResponse.class, produces = "application/json", consumes = "application/json")
    public ForgotPasswordResponse forgotPassword(
	    @ApiParam(value = "Forgot password request") @RequestBody ForgotPasswordRequest forgotPasswordRequest) {

	String email = forgotPasswordRequest.getEmail();
	String secret = forgotPasswordRequest.getSecret();
	String newPassword = passwordEncoder.encode(forgotPasswordRequest.getNewPassword());

	Optional<com.tweetapp.backend.models.User> optional = userRepository.findByEmail(email);

	if (optional.isPresent()) {
	    LOGGER.info("User found for email : {}", email);
	    com.tweetapp.backend.models.User user = optional.get();
	    Optional<ForgotPasswordAnswer> optionalSecret = secretRepository.findById(email);
	    if (optionalSecret.isPresent() && passwordEncoder.matches(secret, optionalSecret.get().getAnswer())) {
		LOGGER.info("User email: {} --secret matched", email);
		user.setPassword(newPassword);
		try {
		    com.tweetapp.backend.models.User user2 = userRepository.save(user);
		    LOGGER.info("User email: {} password updated!!!!", email);
		    ForgotPasswordResponse forgotPasswordResponse = new ForgotPasswordResponse();
		    forgotPasswordResponse.setEmail(user2.getEmail());
		    String jwt = jwtTokenUtil.generateToken(createTmpUser(user2.getEmail(), user2.getPassword()));
		    forgotPasswordResponse.setToken(jwt);
		    return forgotPasswordResponse;
		} catch (RuntimeException e) {
		    LOGGER.error("User email: {} --Exception while saving new password", email);
		    throw new InvalidRequest("Exception :: ", e);
		}
	    } else {
		String errMsg;
		if (optionalSecret.isEmpty()) {
		    errMsg = String.format("User : %s has not shared secret as of now!", email);
		} else {
		    errMsg = String.format("User email: %s secret is not matching", email);
		}
		LOGGER.error(errMsg);
		throw new InvalidRequest(errMsg);
	    }
	} else {
	    LOGGER.error("User email: {} is not registered..", email);
	    throw new InvalidRequest("Invalid REQUIRED user email");
	}

    }

    @GetMapping("/token_validation")
    public String working() {
	return "working...";
    }
}
