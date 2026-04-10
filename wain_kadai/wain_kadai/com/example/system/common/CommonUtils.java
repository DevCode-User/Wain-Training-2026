package com.example.system.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 共通ユーティリティクラス
 */
public class CommonUtils {
    
    /**
     * 文字列がnullまたは空かチェック
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 文字列がnullまたは空でないかチェック
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * 現在時刻を指定フォーマットで取得
     */
    public static String getCurrentDateTime(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }
    
    /**
     * 現在時刻を日本語フォーマットで取得
     */
    public static String getCurrentDateTime() {
        return getCurrentDateTime("yyyy年MM月dd日 HH:mm:ss");
    }
    
    /**
     * 日付をフォーマット
     */
    public static String formatDate(long timestamp, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(timestamp));
    }
    
    /**
     * 金額をフォーマット
     */
    public static String formatCurrency(int amount) {
        return String.format("¥%,d", amount);
    }
    
    /**
     * 数値のバリデーション
     */
    public static boolean isValidNumber(String str) {
        if (isEmpty(str)) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * メールアドレスのバリデーション
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
    
    /**
     * パスワード強度チェック
     */
    public static boolean isStrongPassword(String password) {
        if (isEmpty(password)) {
            return false;
        }
        
        // 8文字以上で、大文字、小文字、数字を含む
        return password.length() >= 8 &&
               password.matches(".*[a-z].*") &&
               password.matches(".*[A-Z].*") &&
               password.matches(".*[0-9].*");
    }
    
    /**
     * ログ出力
     */
    public static void log(String message) {
        System.out.println("[" + getCurrentDateTime() + "] " + message);
    }
    
    /**
     * エラーログ出力
     */
    public static void errorLog(String message) {
        System.err.println("[ERROR " + getCurrentDateTime() + "] " + message);
    }
    
    /**
     * 文字列の先頭と末尾の空白を削除
     */
    public static String trim(String str) {
        return (str != null) ? str.trim() : null;
    }
    
    /**
     * 複数の文字列をカンマで結合
     */
    public static String join(String separator, String... strings) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(strings[i]);
        }
        return sb.toString();
    }
}
