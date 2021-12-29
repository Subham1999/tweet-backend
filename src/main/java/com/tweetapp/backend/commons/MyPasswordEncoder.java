package com.tweetapp.backend.commons;

import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class MyPasswordEncoder {

    /**
     * Nothing fancy here!!
     * Will Update with a concrete hash technique
     * 
     * @param actual
     * @return
     */
    public String encode(String actual) {
	if (actual == null)
	    return null;
	return new String(Base64.getEncoder().encode(actual.getBytes()));
    }
}
