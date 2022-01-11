package com.tweetapp.backend.service.tweet;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(TweetServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private TweetFetchService tweetFetchService;

    @Override
    public CreateTweetResponse createTweet(CreateTweetRequest createTweetRequest) {
	LOGGER.info("Inside 'createTweet'");
	final String createdByEmail = createTweetRequest.getCreatedBy();

	if (userService.userExists(createdByEmail)) {
	    LOGGER.info("CreatedBy -User {} exists", createdByEmail);

	    final Tweet tweet = Tweet.builder().createdBy(createTweetRequest.getCreatedBy())
		    .content(createTweetRequest.getContent()).createdAt(new Date()).likes(new LinkedHashSet<>())
		    .replies(new LinkedList<>()).build();

	    final Tweet savedTweet = tweetRepository.save(tweet);
	    LOGGER.info("Tweet saved tweetId {}", savedTweet.getId());

	    return CreateTweetResponse.builder()._status_code(1)._message("Tweet created")
		    .createdAt(savedTweet.getCreatedAt()).content(savedTweet.getContent())
		    .createdBy(savedTweet.getCreatedBy()).id(savedTweet.getId()).build();

	} else {
	    String message = "Author email id : " + createdByEmail + " is absent or misspelled";
	    LOGGER.error(message);
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

	LOGGER.info("Inside 'likeOnTweet' postId:{}... likerId:{}", likeTweetRequest.getPostId(),
		likeTweetRequest.getLikerId());

	String postId = likeTweetRequest.getPostId();
	String likedBy = likeTweetRequest.getLikerId();

	if (!tweetExists(postId)) {
	    String msg = "Post Id : " + postId + " doesn't exists";
	    LOGGER.error(msg);
	    throw new InvalidRequest(msg);
	}
	if (!userExists(likedBy)) {
	    String msg = "No user with email : " + likedBy + " exist";
	    LOGGER.error(msg);
	    throw new InvalidRequest(msg);
	}

	final Tweet tweet = tweetRepository.findById(postId).get();
	Date likedDate = likeTweetRequest.getLikedDate();
	tweet.getLikes().add(
		Like.builder().likedDate(Objects.isNull(likedDate) ? new Date() : likedDate).likerId(likedBy).build());
	tweetRepository.save(tweet);
	LOGGER.info("Liked on post:{}", postId);
	LinkedHashSet<Like> likes = tweet.getLikes();
	return LikeTweetResponse.builder().likes(likes).build();
    }

    @Override
    public DeleteLikeResponse deleteLikeOnTweet(DeleteLikeRequest deleteLikeRequest) {
	Objects.requireNonNull(deleteLikeRequest);
	LOGGER.info("Inside 'deleteLikeOnTweet'... Post ID:{} Liker :{}", deleteLikeRequest.getPostId(),
		deleteLikeRequest.getLikedBy());
	final String postId = deleteLikeRequest.getPostId();
	final String likedBy = deleteLikeRequest.getLikedBy();

	if (!tweetExists(postId)) {
	    String msg = "Post Id : " + postId + " doesn't exists";
	    LOGGER.error(msg);
	    throw new InvalidRequest(msg);
	}
	if (!userExists(likedBy)) {
	    String msg = "No user with email : " + likedBy + " exist";
	    LOGGER.error(msg);
	    throw new InvalidRequest(msg);
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

	return DeleteLikeResponse.builder().likes(likes).build();
    }

    @Override
    public ReplyTweetResponse replyOnTweet(ReplyTweetRequest replyTweetRequest) {
	Objects.requireNonNull(replyTweetRequest);
	LOGGER.info("Inside 'replyOnTweet'... Post Id {} Replied By {}", replyTweetRequest.getPostId(),
		replyTweetRequest.getRepliedBy());

	String postId = replyTweetRequest.getPostId();
	if (!tweetExists(postId)) {
	    String msg = "Post Id : " + postId + " doesn't exists";
	    LOGGER.error(msg);
	    throw new InvalidRequest(msg);
	}

	String repliedBy = replyTweetRequest.getRepliedBy();

	if (!userExists(repliedBy)) {
	    String msg = "No user with email : " + repliedBy + " exist";
	    LOGGER.error(msg);
	    throw new InvalidRequest(msg);
	}

	Tweet tweet = tweetRepository.findById(postId).get();

	tweet.getReplies().add(Reply.builder().repliedBy(repliedBy).content(replyTweetRequest.getReply())
		.repliedDate(replyTweetRequest.getRepliedDate()).build());

	Tweet save = tweetRepository.save(tweet);
	LinkedList<Reply> replies = save.getReplies();
	LOGGER.info("reply saved successfully");

	return ReplyTweetResponse.builder()._status_code(1)._message("reply saved successfully").replies(replies)
		.build();
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
	LOGGER.info("Inside 'viewTweets'... ");
	final TweetFetchType fetchType = tweetViewConfig.fetchType();
	final Map<TweetViewConfigConstant, Object> configMap = tweetViewConfig.getConfigMap();
	final Pageable pageRequest = tweetViewConfig.getPageRequest();

	if (fetchType == TweetFetchType.GLOBAL_FEED) {
	    LOGGER.info("Fetching 'GLOBAL_FEED'... ");
	    return tweetFetchService.fetchGlobalFeed(pageRequest);
	} else if (fetchType == TweetFetchType.RECENT_POSTS) {
	    final String authorEmail = (String) configMap.get(TweetViewConfigConstant.AUTHOR_NAME);
	    LOGGER.info("Fetching 'RECENT_POSTS'... For user {}", authorEmail);
	    return tweetFetchService.fetchRecentPostsByAuthor(authorEmail, pageRequest);
	} else if (fetchType == TweetFetchType.MOST_LIKE) {
	    final String authorEmail = (String) configMap.get(TweetViewConfigConstant.AUTHOR_NAME);
	    LOGGER.info("Fetching 'MOST_LIKE'... For user {}", authorEmail);
	    return tweetFetchService.fetchMostLikedPostsByAuthor(authorEmail, pageRequest);
	} else {
	    return null;
	}
    }

    @Override
    public Tweet getOne(String tweet_id) {
	LOGGER.info("Inside 'getOne' for Post Id{}", tweet_id);
	Optional<Tweet> optional = tweetRepository.findById(tweet_id);
	if (optional.isEmpty()) {
	    LOGGER.error("Post Id: {} doesn't exists", tweet_id);
	    throw new InvalidRequest("Tweet ID doesn't exist : " + tweet_id);
	}

	LOGGER.info("Post Id: {} exists!!", tweet_id);
	return optional.get();
    }

}
