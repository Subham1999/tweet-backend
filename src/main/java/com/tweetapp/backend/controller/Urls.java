package com.tweetapp.backend.controller;

public class Urls {
    static final String API_VERSION = "v1.0";
    static final String API_PREFIX = "api";

    static final String USER_BASE = API_PREFIX + "/" + API_VERSION + "/" + "users";
    static final String TWEET_BASE = API_PREFIX + "/" + API_VERSION + "/" + "tweets";
    public static final String HEALTH_CHECK = "/health";
}
