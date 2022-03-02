package com.tweetapp.backend.controller;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HealthCheckResponse {
    private String serverStatus;
}
