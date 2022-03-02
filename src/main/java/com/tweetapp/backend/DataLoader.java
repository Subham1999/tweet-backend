package com.tweetapp.backend;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.tweetapp.backend.dto.user.CreateUserRequest;
import com.tweetapp.backend.service.user.UserService;

@Component
public class DataLoader implements ApplicationRunner {

    @Autowired
    private UserService userService;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) throws Exception {
	userService.createUser(createAdminData());
    }

    private CreateUserRequest createAdminData() {
	return CreateUserRequest.builder().firstName("Subham").lastName("Santra").dateOfJoin(new Date())
		.email(adminEmail).password(adminPassword).build();
    }

}
