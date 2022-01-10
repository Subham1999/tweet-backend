package com.tweetapp.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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

@RestController
@RequestMapping(path = "/auth")
public class AuthController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileSecretRepository secretRepository;

    @PostMapping("/generate_token")
    public AuthResponse generateToken(@RequestBody AuthRequest authReq) {

	String password = authReq.getPassword();
	String email = authReq.getEmail();

	Optional<com.tweetapp.backend.models.User> optional = userRepository.findByEmail(email);

	if (optional.isPresent()) {
	    String existingPassword = optional.get().getPassword();
	    if (passwordEncoder.matches(password, existingPassword)) {
		String jwtToken = jwtTokenUtil.generateToken(createTmpUser(authReq.getEmail(), authReq.getPassword()));
		AuthResponse authRes = new AuthResponse(jwtToken);
		return authRes;
	    } else {
		throw new PasswordMismatchException("Password in db is not matching");
	    }
	} else {
	    throw new InvalidRequest("User email is not a registered email");
	}

    }

    private UserDetails createTmpUser(String email, String password) {
	return User.withUsername(email).password(password).authorities(List.of(new SimpleGrantedAuthority("USER")))
		.build();
    }

    @PostMapping("/forgot_password")
    public ForgotPasswordResponse forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
	System.out.println(forgotPasswordRequest);

	String email = forgotPasswordRequest.getEmail();
	String secret = forgotPasswordRequest.getSecret();
	String newPassword = passwordEncoder.encode(forgotPasswordRequest.getNewPassword());

	Optional<com.tweetapp.backend.models.User> optional = userRepository.findByEmail(email);

	if (optional.isPresent()) {
	    com.tweetapp.backend.models.User user = optional.get();
	    Optional<ForgotPasswordAnswer> optionalSecret = secretRepository.findById(email);
	    if (optionalSecret.isPresent() && passwordEncoder.matches(secret, optionalSecret.get().getAnswer())) {
		user.setPassword(newPassword);
		com.tweetapp.backend.models.User user2 = userRepository.save(user);
		ForgotPasswordResponse forgotPasswordResponse = new ForgotPasswordResponse();
		forgotPasswordResponse.setEmail(user2.getEmail());
		String jwt = jwtTokenUtil.generateToken(createTmpUser(user2.getEmail(), user2.getPassword()));
		forgotPasswordResponse.setToken(jwt);
		return forgotPasswordResponse;
	    } else {
		throw new InvalidRequest("Invalid REQUIRED user secret");
	    }
	} else {
	    throw new InvalidRequest("Invalid REQUIRED user email");
	}

    }

    @GetMapping
    public String working() {
	return "working...";
    }
}
