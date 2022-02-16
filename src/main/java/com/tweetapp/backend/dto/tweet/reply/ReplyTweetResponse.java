package com.tweetapp.backend.dto.tweet.reply;

import java.util.List;

import com.tweetapp.backend.dto.Response;
import com.tweetapp.backend.models.Reply;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class ReplyTweetResponse extends Response {

	private List<Reply> replies;
}
