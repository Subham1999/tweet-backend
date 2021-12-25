package com.tweetapp.backend.service.tweet;

import java.util.List;

public interface TweetViewConfig {

    List<String> getAutherNames();

    boolean sorted();

    List<SortingParam> getSortingOrders();
}
