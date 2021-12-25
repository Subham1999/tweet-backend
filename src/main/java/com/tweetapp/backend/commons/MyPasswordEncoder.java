package com.tweetapp.backend.commons;

import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class MyPasswordEncoder {

    public String encode(String actual) {
	if (actual == null)
	    return null;
	return new String(Base64.getEncoder().encode(actual.getBytes()));
    }
}
