package com.shopflow.main.security;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory store for refresh tokens (use Redis in production).
 */
@Component
public class RefreshTokenStore {

    private final Set<String> validTokens = ConcurrentHashMap.newKeySet();
    private final Set<String> blacklisted = ConcurrentHashMap.newKeySet();

    public void store(String token) {
        validTokens.add(token);
    }

    public boolean isValid(String token) {
        return validTokens.contains(token) && !blacklisted.contains(token);
    }

    public void invalidate(String token) {
        blacklisted.add(token);
        validTokens.remove(token);
    }
}