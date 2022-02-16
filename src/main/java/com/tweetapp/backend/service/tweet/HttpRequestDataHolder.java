package com.tweetapp.backend.service.tweet;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Scope("request")
@Data
public class HttpRequestDataHolder {
	private String user;
}
