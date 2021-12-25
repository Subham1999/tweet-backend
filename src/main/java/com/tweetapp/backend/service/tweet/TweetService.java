package com.tweetapp.backend.service.tweet;

import org.springframework.data.domain.Page;

import com.tweetapp.backend.dto.tweet.CreateTweetRequest;
import com.tweetapp.backend.dto.tweet.CreateTweetResponse;
import com.tweetapp.backend.dto.tweet.DeleteTweetRequest;
import com.tweetapp.backend.dto.tweet.DeleteTweetResponse;
import com.tweetapp.backend.dto.tweet.UpdateTweetRequest;
import com.tweetapp.backend.dto.tweet.UpdateTweetResponse;
import com.tweetapp.backend.dto.tweet.ViewTweetRequest;
import com.tweetapp.backend.dto.tweet.ViewTweetResponse;
import com.tweetapp.backend.dto.tweet.like.DeleteLikeRequest;
import com.tweetapp.backend.dto.tweet.like.DeleteLikeResponse;
import com.tweetapp.backend.dto.tweet.like.LikeTweetRequest;
import com.tweetapp.backend.dto.tweet.like.LikeTweetResponse;
import com.tweetapp.backend.dto.tweet.reply.DeleteReplyRequest;
import com.tweetapp.backend.dto.tweet.reply.DeleteReplyResponse;
import com.tweetapp.backend.dto.tweet.reply.ReplyTweetRequest;
import com.tweetapp.backend.dto.tweet.reply.ReplyTweetResponse;
import com.tweetapp.backend.models.Tweet;

public interface TweetService {

    CreateTweetResponse createTweet(final CreateTweetRequest createTweetRequest);

    ViewTweetResponse viewTweet(final ViewTweetRequest viewTweetRequest);

    UpdateTweetResponse updateTweet(final UpdateTweetRequest updateTweetRequest);

    DeleteTweetResponse deleteTweet(final DeleteTweetRequest deleteTweetRequest);

    LikeTweetResponse likeOnTweet(final LikeTweetRequest likeTweetRequest);

    DeleteLikeResponse deleteLikeOnTweet(final DeleteLikeRequest deleteLikeRequest);

    ReplyTweetResponse replyOnTweet(final ReplyTweetRequest replyTweetRequest);

    DeleteReplyResponse deleteReplyOnTweet(final DeleteReplyRequest deleteReplyRequest);

    Page<Tweet> viewTweets(final TweetViewConfig tweetViewConfig);
}
