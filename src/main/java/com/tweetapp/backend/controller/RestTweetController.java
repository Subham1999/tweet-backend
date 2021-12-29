package com.tweetapp.backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.backend.dto.tweet.CreateTweetRequest;
import com.tweetapp.backend.dto.tweet.CreateTweetResponse;
import com.tweetapp.backend.dto.tweet.reply.ReplyTweetRequest;
import com.tweetapp.backend.dto.tweet.reply.ReplyTweetResponse;
import com.tweetapp.backend.models.Tweet;
import com.tweetapp.backend.service.tweet.TweetFetchType;
import com.tweetapp.backend.service.tweet.TweetService;
import com.tweetapp.backend.service.tweet.TweetViewConfig;
import com.tweetapp.backend.service.tweet.TweetViewConfigConstant;

@RestController
@RequestMapping(path = Urls.TWEET_BASE)
public class RestTweetController {

    @Autowired
    private TweetService tweetService;

    @GetMapping
    public String hello() {
	return "Hello";
    }

    @PostMapping
    public ResponseEntity<CreateTweetResponse> postNewTweet(@RequestBody CreateTweetRequest createTweetRequest) {

	CreateTweetResponse createTweetResponse = tweetService.createTweet(createTweetRequest);

	if (createTweetResponse.get_status_code() == 0) {
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createTweetResponse);
	} else {
	    return ResponseEntity.ok(createTweetResponse);
	}
    }

    @GetMapping("/search")
    public Page<Tweet> searchTweets(@RequestParam(required = false) String createdBy,
	    @RequestParam(required = false, defaultValue = "globalFeed") String fetchType,
	    @RequestParam(required = false, defaultValue = "0") Integer pageOffset,
	    @RequestParam(required = false, defaultValue = "10") Integer pageLength,
	    @RequestParam(required = false, defaultValue = "createdAt_DESCENDING") String[] sortBy) {
	TweetFetchType tweetFetchType;

	System.out.println("Created by : " + createdBy);

	if (StringUtils.equalsIgnoreCase(fetchType, "globalfeed")) {
	    tweetFetchType = TweetFetchType.GLOBAL_FEED;
	} else if (StringUtils.equalsIgnoreCase(fetchType, "mostlikes")) {
	    tweetFetchType = TweetFetchType.MOST_LIKE;
	} else if (StringUtils.equalsIgnoreCase(fetchType, "recentposts")) {
	    tweetFetchType = TweetFetchType.RECENT_POSTS;
	} else {
	    tweetFetchType = TweetFetchType.GLOBAL_FEED;
	}

	Map<TweetViewConfigConstant, Object> map = new HashMap<>();

	if (!tweetFetchType.equals(TweetFetchType.GLOBAL_FEED)) {
	    map.put(TweetViewConfigConstant.AUTHOR_NAME, createdBy);
	}

	return tweetService.viewTweets(new TweetViewConfig() {

	    @Override
	    public Pageable getPageRequest() {
		final List<Order> orders = new ArrayList<>();
		for (final String sortByParam : sortBy) {
		    if (sortByParam.endsWith("_DESCENDING")) {
			orders.add(Order.desc(sortByParam.split("_DESCENDING")[0]));
		    } else {
			orders.add(Order.asc(sortByParam));
		    }
		}
		System.out.println(orders);
		final PageRequest pageRequest = PageRequest.of(pageOffset, pageLength, Sort.by(orders));
		return pageRequest;
	    }

	    @Override
	    public Map<TweetViewConfigConstant, Object> getConfigMap() {
		return map;
	    }

	    @Override
	    public TweetFetchType fetchType() {
		return tweetFetchType;
	    }
	});
    }

    @GetMapping("/{tweet_id}")
    public Tweet getOne(@PathVariable String tweet_id) {
	return tweetService.getOne(tweet_id);
    }

    @PostMapping("/reply")
    public ResponseEntity<Object> replyOnPost(@RequestBody @Valid ReplyTweetRequest replyTweetRequest) {
	ReplyTweetResponse replyTweetResponse = tweetService.replyOnTweet(replyTweetRequest);
	return ResponseEntity.ok(replyTweetResponse);
    }

}
