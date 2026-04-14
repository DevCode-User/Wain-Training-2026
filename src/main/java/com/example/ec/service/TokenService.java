package com.example.ec.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {
    private final Map<String, TokenUser> tokenStore = new ConcurrentHashMap<>();
    public String issueToken(Integer userPk, String loginId, String role) {
        String token = UUID.randomUUID().toString().replace("-", "");
        tokenStore.put(token, new TokenUser(userPk, loginId, role));
        return token;
    }
    public TokenUser resolve(String token) { return tokenStore.get(token); }
    public static class TokenUser {
        private final Integer userPk;
        private final String loginId;
        private final String role;
        public TokenUser(Integer userPk, String loginId, String role) {
            this.userPk = userPk; this.loginId = loginId; this.role = role;
        }
        public Integer getUserPk() { return userPk; }
        public String getLoginId() { return loginId; }
        public String getRole() { return role; }
    }
}
