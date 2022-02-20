package com.tweetapp.backend;

import static springfox.documentation.builders.PathSelectors.regex;

import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tweetapp.backend.configs.SwaggerConfigProps;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableWebSecurity
@EnableAspectJAutoProxy
public class TweetBackendApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(TweetBackendApplication.class);

    public static void main(String[] args) {
	LOGGER.info("TweetBackendApplication starting...");
	SpringApplication.run(TweetBackendApplication.class, args);
    }

    @Autowired
    private SwaggerConfigProps configProps;

    @Bean
    public Docket postsApi() {
	return new Docket(DocumentationType.SWAGGER_2).groupName(configProps.getGroup_name()).apiInfo(apiInfo())
		.select().paths(postPaths()).build();
    }

    private Predicate<String> postPaths() {
	return regex(configProps.getApi_regex());
    }

    private ApiInfo apiInfo() {
	return new ApiInfoBuilder().title(configProps.getTitle()).description(configProps.getDescription())
		.contact(new Contact(configProps.getContact_name(), configProps.getContact_url(),
			configProps.getContact_email()))
		.license(configProps.getLicense()).licenseUrl(configProps.getLicense_url())
		.version(configProps.getVersion()).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
	LOGGER.info("PasswordEncoder Bean method invoked");
	return new BCryptPasswordEncoder();
    }
}
