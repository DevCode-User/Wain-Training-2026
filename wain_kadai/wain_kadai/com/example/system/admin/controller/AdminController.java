package com.example.system.admin.controller;

import com.example.system.common.AuthMannager;
import com.example.system.common.User;

/**
 * 管理画面のコントローラー
 */
public class AdminController {
    
    private AuthMannager authMannager;
    
    public AdminController() {
        // AuthMannagerを初期化
    }
    
    /**
     * ログイン処理
     * @param loginRequest ログインリクエスト
     * @return ログインレスポンス
     */
    public LoginResponse login(LoginRequest loginRequest) {
        if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            return new LoginResponse(false, "ユーザー名またはパスワードが空です");
        }
        
        String username = loginRequest.getUsername().trim();
        String password = loginRequest.getPassword();
        
        // 空文字列チェック
        if (username.isEmpty() || password.isEmpty()) {
            return new LoginResponse(false, "ユーザー名またはパスワードを入力してください");
        }
        
        // 認証処理
        LoginResponse response = AuthMannager.login(username, password);
        return response;
    }
    
    /**
     * ログアウト処理
     * @param token トークン
     * @return ログアウト結果
     */
    public boolean logout(String token) {
        return AuthMannager.logout(token);
    }
    
    /**
     * トークン検証
     * @param token トークン
     * @return トークンが有効な場合true
     */
    public boolean validateToken(String token) {
        return AuthMannager.validateToken(token);
    }
    
    /**
     * ダッシュボードデータを取得
     * @param token トークン
     * @return ダッシュボード情報
     */
    public String getDashboard(String token) {
        // トークンの検証
        if (!AuthMannager.validateToken(token)) {
            return "Unauthorized";
        }
        
        User user = AuthMannager.getUserByToken(token);
        if (user == null) {
            return "Unauthorized";
        }
        
        // 管理者権限の確認
        if (!"ADMIN".equals(user.getRole())) {
            return "Forbidden";
        }
        
        // ダッシュボードデータを返す（JSON形式）
        return "{" +
                "\"status\": \"success\", " +
                "\"username\": \"" + user.getUsername() + "\", " +
                "\"role\": \"" + user.getRole() + "\"" +
                "}";
    }
    
    /**
     * ユーザー情報を取得
     * @param token トークン
     * @return ユーザー情報
     */
    public User getCurrentUser(String token) {
        if (!AuthMannager.validateToken(token)) {
            return null;
        }
        
        return AuthMannager.getUserByToken(token);
    }
}