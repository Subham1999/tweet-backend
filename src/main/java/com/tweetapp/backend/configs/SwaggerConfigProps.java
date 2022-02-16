package com.tweetapp.backend.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@ConfigurationProperties(value = "swagger-api-doc")
@Configuration
public class SwaggerConfigProps {
	private String group_name;
	private String title;
	private String api_regex;
	private String description;
	private String contact_name;
	private String contact_url;
	private String contact_email;
	private String license;
	private String license_url;
	private String version;
}
