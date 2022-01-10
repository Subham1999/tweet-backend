package com.tweetapp.backend.service.tweet;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
import com.tweetapp.backend.exceptions.InvalidRequest;
import com.tweetapp.backend.models.Like;
import com.tweetapp.backend.models.Reply;
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
		    .content(createTweetRequest.getContent()).createdAt(new Date()).likes(new LinkedHashSet<>())
		    .replies(new LinkedList<>()).build();

	    final Tweet savedTweet = tweetRepository.save(tweet);

	    return CreateTweetResponse.builder()._status_code(1)._message("Tweet created")
		    .createdAt(savedTweet.getCreatedAt()).content(savedTweet.getContent())
		    .createdBy(savedTweet.getCreatedBy()).id(savedTweet.getId()).build();

	} else {
	    String message = "Author email id : " + createdByEmail + " is absent or misspelled";
	    throw new InvalidRequest(message);
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
	Objects.requireNonNull(likeTweetRequest);

	String postId = likeTweetRequest.getPostId();
	String likedBy = likeTweetRequest.getLikerId();

	if (!tweetExists(postId)) {
	    throw new InvalidRequest("Post Id : " + postId + " doesn't exists");
	}
	if (!userExists(likedBy)) {
	    throw new InvalidRequest("No user with email : " + likedBy + " exist");
	}

	final Tweet tweet = tweetRepository.findById(postId).get();
	Date likedDate = likeTweetRequest.getLikedDate();
	tweet.getLikes().add(
		Like.builder().likedDate(Objects.isNull(likedDate) ? new Date() : likedDate).likerId(likedBy).build());
	tweetRepository.save(tweet);
	return LikeTweetResponse.builder()._status_code(1)._message("Liked successfully").build();
    }

    @Override
    public DeleteLikeResponse deleteLikeOnTweet(DeleteLikeRequest deleteLikeRequest) {
	Objects.requireNonNull(deleteLikeRequest);
	final String postId = deleteLikeRequest.getPostId();
	final String likedBy = deleteLikeRequest.getLikedBy();

	if (!tweetExists(postId)) {
	    throw new InvalidRequest("Post Id : " + postId + " doesn't exists");
	}
	if (!userExists(likedBy)) {
	    throw new InvalidRequest("No user with email : " + likedBy + " exist");
	}

	final Like like = Like.builder().likerId(likedBy).likedDate(null).build();
	final Tweet tweet = tweetRepository.findById(postId).get();
	final LinkedHashSet<Like> likes = tweet.getLikes();

	if (likes.contains(like)) {
	    likes.remove(like);
	    tweetRepository.save(tweet);
	} else {
	    // Nothing TODO
	}

	return DeleteLikeResponse.builder()._status_code(1).build();
    }

    @Override
    public ReplyTweetResponse replyOnTweet(ReplyTweetRequest replyTweetRequest) {
	Objects.requireNonNull(replyTweetRequest);

	String postId = replyTweetRequest.getPostId();
	if (!tweetExists(postId)) {
	    throw new InvalidRequest("Post Id : " + postId + " doesn't exists");
	}

	String repliedBy = replyTweetRequest.getRepliedBy();

	if (!userExists(repliedBy)) {
	    throw new InvalidRequest("No user with email : " + repliedBy + " exist");
	}

	Tweet tweet = tweetRepository.findById(postId).get();

	tweet.getReplies().add(Reply.builder().repliedBy(repliedBy).content(replyTweetRequest.getReply())
		.repliedDate(replyTweetRequest.getRepliedDate()).build());

	tweetRepository.save(tweet);

	return ReplyTweetResponse.builder()._status_code(1)._message("reply saved successfully").build();
    }

    private boolean userExists(String repliedBy) {
	return userService.userExists(repliedBy);
    }

    private boolean tweetExists(String postId) {
	return tweetRepository.existsById(postId);
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

    @Override
    public Tweet getOne(String tweet_id) {
	Optional<Tweet> optional = tweetRepository.findById(tweet_id);
	if (optional.isEmpty()) {
	    throw new InvalidRequest("Tweet ID doesn't exist : " + tweet_id);
	}

	return optional.get();
    }

}
