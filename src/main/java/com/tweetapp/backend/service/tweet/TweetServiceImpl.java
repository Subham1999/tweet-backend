package com.tweetapp.backend.service.tweet;

import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tweetapp.backend.dao.tweet.TweetRepository;
import com.tweetapp.backend.dto.tweet.CreateTweetRequest;
import com.tweetapp.backend.dto.tweet.CreateTweetResponse;
import com.tweetapp.backend.dto.tweet.DeleteTweetRequest;
import com.tweetapp.backend.dto.tweet.DeleteTweetResponse;
import com.tweetapp.backend.dto.tweet.UpdateTweetRequest;
import com.tweetapp.backend.dto.tweet.UpdateTweetResponse;
import com.tweetapp.backend.dto.tweet.like.DeleteLikeRequest;
import com.tweetapp.backend.dto.tweet.like.DeleteLikeResponse;
import com.tweetapp.backend.dto.tweet.like.LikeTweetRequest;
import com.tweetapp.backend.dto.tweet.like.LikeTweetResponse;
import com.tweetapp.backend.dto.tweet.reply.DeleteReplyRequest;
import com.tweetapp.backend.dto.tweet.reply.DeleteReplyResponse;
import com.tweetapp.backend.dto.tweet.reply.ReplyTweetRequest;
import com.tweetapp.backend.dto.tweet.reply.ReplyTweetResponse;
import com.tweetapp.backend.models.Tweet;
import com.tweetapp.backend.service.user.UserService;

@Service
public class TweetServiceImpl implements TweetService {

    @Autowired
    private UserService userService;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private TweetFetchService tweetFetchService;

    @Override
    public CreateTweetResponse createTweet(CreateTweetRequest createTweetRequest) {

	final String createdByEmail = createTweetRequest.getCreatedBy();

	if (userService.userExists(createdByEmail)) {

	    final Tweet tweet = Tweet.builder().createdBy(createTweetRequest.getCreatedBy())
		    .content(createTweetRequest.getContent()).createdAt(new Date()).likes(new LinkedList<>())
		    .replies(new LinkedList<>()).build();

	    final Tweet savedTweet = tweetRepository.save(tweet);

	    return CreateTweetResponse.builder()._status_code(1)._message("Tweet created")
		    .createdAt(savedTweet.getCreatedAt()).content(savedTweet.getContent())
		    .createdBy(savedTweet.getCreatedBy()).id(savedTweet.getId()).build();

	} else {
	    return CreateTweetResponse.builder()._status_code(1)
		    ._message("Author email id : " + createdByEmail + " is absent or misspelled").build();
	}

    }

    @Override
    public UpdateTweetResponse updateTweet(UpdateTweetRequest updateTweetRequest) {
	return null;
    }

    @Override
    public DeleteTweetResponse deleteTweet(DeleteTweetRequest deleteTweetRequest) {
	return null;
    }

    @Override
    public LikeTweetResponse likeOnTweet(LikeTweetRequest likeTweetRequest) {
	return null;
    }

    @Override
    public DeleteLikeResponse deleteLikeOnTweet(DeleteLikeRequest deleteLikeRequest) {
	return null;
    }

    @Override
    public ReplyTweetResponse replyOnTweet(ReplyTweetRequest replyTweetRequest) {
	return null;
    }

    @Override
    public DeleteReplyResponse deleteReplyOnTweet(DeleteReplyRequest deleteReplyRequest) {
	return null;
    }

    @Override
    public Page<Tweet> viewTweets(TweetViewConfig tweetViewConfig) {

	final TweetFetchType fetchType = tweetViewConfig.fetchType();
	final Map<TweetViewConfigConstant, Object> configMap = tweetViewConfig.getConfigMap();
	final Pageable pageRequest = tweetViewConfig.getPageRequest();

	if (fetchType == TweetFetchType.GLOBAL_FEED) {
	    return tweetFetchService.fetchGlobalFeed(pageRequest);
	} else if (fetchType == TweetFetchType.RECENT_POSTS) {
	    final String authorEmail = (String) configMap.get(TweetViewConfigConstant.AUTHOR_NAME);
	    return tweetFetchService.fetchRecentPostsByAuthor(authorEmail, pageRequest);
	} else if (fetchType == TweetFetchType.MOST_LIKE) {
	    final String authorEmail = (String) configMap.get(TweetViewConfigConstant.AUTHOR_NAME);
	    return tweetFetchService.fetchMostLikedPostsByAuthor(authorEmail, pageRequest);
	} else {
	    return null;
	}
    }

}
