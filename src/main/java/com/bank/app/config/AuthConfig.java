package com.bank.app.config;

import lombok.Getter;

@Getter
public class AuthConfig {
    private boolean authEnabled;
    private String baseUrl;
    private String clientId;
    private String issuer;
    private String audience;
    private String apiToken;
}