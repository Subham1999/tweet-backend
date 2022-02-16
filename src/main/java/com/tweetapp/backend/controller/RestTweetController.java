package com.tweetapp.backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.backend.dto.tweet.CreateTweetRequest;
import com.tweetapp.backend.dto.tweet.CreateTweetResponse;
import com.tweetapp.backend.dto.tweet.UpdateTweetRequest;
import com.tweetapp.backend.dto.tweet.UpdateTweetResponse;
import com.tweetapp.backend.dto.tweet.like.DeleteLikeRequest;
import com.tweetapp.backend.dto.tweet.like.DeleteLikeResponse;
import com.tweetapp.backend.dto.tweet.like.LikeTweetRequest;
import com.tweetapp.backend.dto.tweet.like.LikeTweetResponse;
import com.tweetapp.backend.dto.tweet.reply.ReplyTweetRequest;
import com.tweetapp.backend.dto.tweet.reply.ReplyTweetResponse;
import com.tweetapp.backend.models.Tweet;
import com.tweetapp.backend.service.tweet.TweetFetchType;
import com.tweetapp.backend.service.tweet.TweetService;
import com.tweetapp.backend.service.tweet.TweetViewConfig;
import com.tweetapp.backend.service.tweet.TweetViewConfigConstant;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(path = Urls.TWEET_BASE)
@CrossOrigin
public class RestTweetController {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestTweetController.class);

	@Autowired
	private TweetService tweetService;

	@GetMapping("/hello")
	public String hello() {
		LOGGER.info("Inside 'hello'");
		return "Hello";
	}

	@PostMapping
	public CreateTweetResponse postNewTweet(@RequestBody CreateTweetRequest createTweetRequest) {
		LOGGER.info("Inside 'postNewTweet'");
		return tweetService.createTweet(createTweetRequest);
	}

	@GetMapping("/search")
	public Page<Tweet> searchTweets(@RequestParam(required = false, name = "created_by") String createdBy,
			@RequestParam(required = false, defaultValue = "globalFeed") String fetchType,
			@RequestParam(required = false, defaultValue = "0") Integer pageOffset,
			@RequestParam(required = false, defaultValue = "10") Integer pageLength,
			@RequestParam(required = false, defaultValue = "createdAt_DESCENDING") String[] sortBy) {

		LOGGER.info("Inside 'searchTweets', CreatedBy {}, fetchType {}, pageOffset {}, pageLength {}", createdBy,
				fetchType, pageOffset, pageLength);

		TweetFetchType tweetFetchType;

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
		LOGGER.info("Inside 'getOne'");
		return tweetService.getOne(tweet_id);
	}

	@PutMapping("/{tweet_id}")
	@ApiOperation(value = "Updates a tweet", notes = "Updates an already created tweet. tweet author and updator should be same user!!")
	public UpdateTweetResponse updateOne(@PathVariable String tweet_id,
			@RequestBody UpdateTweetRequest updateTweetRequest) {
		LOGGER.info("Inside 'updateOne...' tweet_id : {}", tweet_id);
		return tweetService.updateTweet(updateTweetRequest);
	}

	@PostMapping("/reply")
	public ResponseEntity<Object> replyOnPost(@RequestBody @Valid ReplyTweetRequest replyTweetRequest) {
		LOGGER.info("Inside 'replyOnPost'");
		ReplyTweetResponse replyTweetResponse = tweetService.replyOnTweet(replyTweetRequest);
		return ResponseEntity.ok(replyTweetResponse);
	}

	@PostMapping("/like")
	public LikeTweetResponse likeOnPost(@RequestBody @Valid LikeTweetRequest likeTweetRequest) {
		LOGGER.info("Inside 'likeOnPost'");
		return tweetService.likeOnTweet(likeTweetRequest);
	}

	@PostMapping("/dislike")
	public DeleteLikeResponse dislikeOnPost(@RequestBody @Valid DeleteLikeRequest deleteLikeRequest) {
		LOGGER.info("Inside 'dislikeOnPost'");
		return tweetService.deleteLikeOnTweet(deleteLikeRequest);
	}

}
