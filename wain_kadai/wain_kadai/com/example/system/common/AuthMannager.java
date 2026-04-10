package com.example.system.common;

import com.example.system.admin.controller.LoginResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 認証・認可を管理するクラス
 */
public class AuthMannager {
    
    // ユーザーデータベース（本来はDB）
    private static Map<String, User> userDatabase = new HashMap<>();
    
    // トークンデータベース（本来はDB/キャッシュ）
    private static Map<String, UserSession> sessionMap = new HashMap<>();
    
    static {
        // デモ用のサンプルユーザーを初期化
        initializeUsers();
    }
    
    /**
     * デモ用のサンプルユーザーを初期化
     */
    private static void initializeUsers() {
        // 管理者
        userDatabase.put("admin", new User(
            "admin_001",
            "admin",
            hashPassword("admin123"),
            "admin@example.com",
            "ADMIN"
        ));
        
        // 通常ユーザー
        userDatabase.put("user1", new User(
            "user_001",
            "user1",
            hashPassword("user123"),
            "user1@example.com",
            "USER"
        ));
        
        userDatabase.put("user2", new User(
            "user_002",
            "user2",
            hashPassword("user123"),
            "user2@example.com",
            "USER"
        ));
    }
    
    /**
     * ログイン処理
     */
    public static LoginResponse login(String username, String password) {
        // ユーザーの存在確認
        User user = userDatabase.get(username);
        if (user == null) {
            return new LoginResponse(false, "ユーザーが見つかりません");
        }
        
        // ユーザーが有効状態か確認
        if (!user.isActive()) {
            return new LoginResponse(false, "このアカウントは無効化されています");
        }
        
        // パスワード検証
        if (!verifyPassword(password, user.getPassword())) {
            return new LoginResponse(false, "パスワードが間違っています");
        }
        
        // トークン生成
        String token = generateToken();
        
        // セッション情報を保存
        UserSession session = new UserSession(
            token,
            user.getUserId(),
            user.getUsername(),
            user.getRole(),
            System.currentTimeMillis(),
            System.currentTimeMillis() + (24 * 60 * 60 * 1000) // 24時間有効
        );
        sessionMap.put(token, session);
        
        return new LoginResponse(true, "ログインに成功しました", token, user.getUsername(), user.getRole());
    }
    
    /**
     * ログアウト処理
     */
    public static boolean logout(String token) {
        return sessionMap.remove(token) != null;
    }
    
    /**
     * トークンの妥当性を検証
     */
    public static boolean validateToken(String token) {
        UserSession session = sessionMap.get(token);
        if (session == null) {
            return false;
        }
        
        // トークンの有効期限を確認
        if (System.currentTimeMillis() > session.getExpiresAt()) {
            sessionMap.remove(token);
            return false;
        }
        
        return true;
    }
    
    /**
     * トークンからユーザー情報を取得
     */
    public static User getUserByToken(String token) {
        UserSession session = sessionMap.get(token);
        if (session == null || !validateToken(token)) {
            return null;
        }
        
        return userDatabase.get(session.getUsername());
    }
    
    /**
     * ユーザーを登録
     */
    public static boolean registerUser(String username, String password, String email) {
        // ユーザーが既に存在するか確認
        if (userDatabase.containsKey(username)) {
            return false;
        }
        
        // 新規ユーザーを作成
        User newUser = new User(
            "user_" + UUID.randomUUID().toString().substring(0, 8),
            username,
            hashPassword(password),
            email,
            "USER"
        );
        
        userDatabase.put(username, newUser);
        return true;
    }
    
    /**
     * パスワードをハッシュ化（簡易実装）
     * 実際はbcryptやArgon2を使用すること
     */
    private static String hashPassword(String password) {
        // 簡易実装：実際はセキュアなハッシュ関数を使用
        return "hashed_" + password;
    }
    
    /**
     * パスワードを検証
     */
    private static boolean verifyPassword(String rawPassword, String hashedPassword) {
        return hashPassword(rawPassword).equals(hashedPassword);
    }
    
    /**
     * トークンを生成
     */
    private static String generateToken() {
        return "token_" + UUID.randomUUID().toString();
    }
    
    /**
     * セッション情報を表すクラス
     */
    public static class UserSession {
        private String token;
        private String userId;
        private String username;
        private String role;
        private long createdAt;
        private long expiresAt;
        
        public UserSession(String token, String userId, String username, String role, long createdAt, long expiresAt) {
            this.token = token;
            this.userId = userId;
            this.username = username;
            this.role = role;
            this.createdAt = createdAt;
            this.expiresAt = expiresAt;
        }
        
        // Getters
        public String getToken() { return token; }
        public String getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getRole() { return role; }
        public long getCreatedAt() { return createdAt; }
        public long getExpiresAt() { return expiresAt; }
    }
}