package com.tnh.kiosk.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String jwtSecret;
    private long jwtExpirationMs;
    private Map<String, String> users; // username -> password

    // getters & setters
    public String getJwtSecret() { return jwtSecret; }
    public void setJwtSecret(String jwtSecret) { this.jwtSecret = jwtSecret; }
    public long getJwtExpirationMs() { return jwtExpirationMs; }
    public void setJwtExpirationMs(long jwtExpirationMs) { this.jwtExpirationMs = jwtExpirationMs; }
    public Map<String, String> getUsers() { return users; }
    public void setUsers(Map<String, String> users) { this.users = users; }
}