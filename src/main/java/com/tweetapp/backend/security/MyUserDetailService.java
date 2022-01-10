package com.tweetapp.backend.security;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.tweetapp.backend.dao.user.UserRepository;
import com.tweetapp.backend.models.User;

@Component
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
	final Optional<User> optionalUser = userRepository.findByEmail(email);
	if (optionalUser.isEmpty()) {
	    throw new UsernameNotFoundException("user email is not registered : " + email);
	}
	User user = optionalUser.get();
	return org.springframework.security.core.userdetails.User.withUsername(email).password(user.getPassword())
		.authorities(List.of(new SimpleGrantedAuthority("USER"))).build();
    }

}
