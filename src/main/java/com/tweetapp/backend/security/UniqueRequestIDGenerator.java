package com.tweetapp.backend.security;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class UniqueRequestIDGenerator {

    private final char[] allDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    private final Random random = new Random();

    public String get() {
	char[] digits = new char[10];
	for (int idx = 0; idx < digits.length; ++idx) {
	    digits[idx] = allDigits[random.nextInt(allDigits.length)];
	}
	return new String(digits);
    }
}
