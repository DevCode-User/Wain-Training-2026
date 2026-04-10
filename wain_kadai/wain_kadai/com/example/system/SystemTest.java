package com.example.system;

import com.example.system.admin.controller.AdminController;
import com.example.system.admin.controller.LoginRequest;
import com.example.system.admin.controller.LoginResponse;
import com.example.system.admin.service.ProductService;
import com.example.system.common.AuthMannager;
import com.example.system.common.CommonUtils;
import com.example.system.common.User;
import com.example.system.customer.service.OrderService;

import java.util.ArrayList;
import java.util.List;

/**
 * システム動作確認用メインクラス
 */
public class SystemTest {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  WAIN KADAI SYSTEM - 動作確認");
        System.out.println("========================================\n");
        
        // 1. ログイン機能のテスト
        testLoginFunction();
        
        // 2. 商品管理機能のテスト
        testProductFunction();
        
        // 3. 注文管理機能のテスト
        testOrderFunction();
        
        // 4. ユーティリティ機能のテスト
        testUtilityFunction();
        
        System.out.println("\n========================================");
        System.out.println("  すべてのテストが完了しました！");
        System.out.println("========================================");
    }
    
    /**
     * ログイン機能のテスト
     */
    private static void testLoginFunction() {
        System.out.println("\n[TEST 1] ログイン機能\n");
        
        AdminController controller = new AdminController();
        
        // テスト1: 正常なログイン
        System.out.println("1-1. 正常なログイン (admin / admin123)");
        LoginRequest validRequest = new LoginRequest("admin", "admin123");
        LoginResponse response = controller.login(validRequest);
        printLoginResponse(response);
        
        String token = response.getToken();
        
        // テスト2: 不正なパスワード
        System.out.println("\n1-2. 不正なパスワード (admin / wrongpass)");
        LoginRequest invalidPasswordRequest = new LoginRequest("admin", "wrongpass");
        response = controller.login(invalidPasswordRequest);
        printLoginResponse(response);
        
        // テスト3: 存在しないユーザー
        System.out.println("\n1-3. 存在しないユーザー (unknown / unknown123)");
        LoginRequest invalidUserRequest = new LoginRequest("unknown", "unknown123");
        response = controller.login(invalidUserRequest);
        printLoginResponse(response);
        
        // テスト4: トークン検証
        if (token != null) {
            System.out.println("\n1-4. トークン検証");
            boolean isValid = AuthMannager.validateToken(token);
            System.out.println("トークンは有効です: " + isValid);
        }
        
        // テスト5: ユーザー情報取得
        if (token != null) {
            System.out.println("\n1-5. トークンからユーザー情報を取得");
            User user = AuthMannager.getUserByToken(token);
            if (user != null) {
                System.out.println("ユーザー: " + user.getUsername());
                System.out.println("ロール: " + user.getRole());
                System.out.println("メール: " + user.getEmail());
            }
        }
    }
    
    /**
     * 商品管理機能のテスト
     */
    private static void testProductFunction() {
        System.out.println("\n[TEST 2] 商品管理機能\n");
        
        // テスト1: 全商品取得
        System.out.println("2-1. 全商品を取得");
        List<ProductService.Product> products = ProductService.getAllProducts();
        System.out.println("取得した商品数: " + products.size());
        for (ProductService.Product p : products) {
            System.out.println("  - ID: " + p.getId() + ", 名前: " + p.getName() + 
                             ", 価格: ¥" + p.getPrice() + ", 在庫: " + p.getStock());
        }
        
        // テスト2: IDで商品取得
        System.out.println("\n2-2. IDで商品を取得 (ID=1)");
        ProductService.Product product = ProductService.getProductById(1);
        if (product != null) {
            System.out.println("商品: " + product.getName());
            System.out.println("説明: " + product.getDescription());
            System.out.println("価格: ¥" + product.getPrice());
            System.out.println("在庫: " + product.getStock() + "個");
        }
        
        // テスト3: 新規商品追加
        System.out.println("\n2-3. 新規商品を追加");
        ProductService.Product newProduct = new ProductService.Product(
            100, "テスト商品", "このは試験用の商品です", 5000, 10
        );
        boolean added = ProductService.addProduct(newProduct);
        System.out.println("追加成功: " + added);
        
        // テスト4: 商品更新
        System.out.println("\n2-4. 商品の在庫を更新 (ID=100, 新在庫=5)");
        newProduct.setStock(5);
        boolean updated = ProductService.updateProduct(newProduct);
        System.out.println("更新成功: " + updated);
        
        // 確認
        ProductService.Product updatedProduct = ProductService.getProductById(100);
        if (updatedProduct != null) {
            System.out.println("更新後の在庫: " + updatedProduct.getStock() + "個");
        }
    }
    
    /**
     * 注文管理機能のテスト
     */
    private static void testOrderFunction() {
        System.out.println("\n[TEST 3] 注文管理機能\n");
        
        // テスト1: 注文を作成
        System.out.println("3-1. 新規注文を作成");
        List<OrderService.OrderItem> items = new ArrayList<>();
        items.add(new OrderService.OrderItem(1, "商品A", 2, 1000));
        items.add(new OrderService.OrderItem(2, "商品B", 1, 2500));
        
        OrderService.Order order = OrderService.createOrder(
            "customer_001",
            "田中太郎",
            items,
            4500
        );
        
        System.out.println("注文ID: " + order.getOrderId());
        System.out.println("顧客名: " + order.getCustomerName());
        System.out.println("合計金額: ¥" + order.getTotalAmount());
        System.out.println("ステータス: " + order.getStatus());
        System.out.println("注文内容:");
        for (OrderService.OrderItem item : order.getItems()) {
            System.out.println("  - " + item.getProductName() + " ×" + item.getQuantity() + 
                             " = ¥" + item.getTotal());
        }
        
        // テスト2: ステータス更新
        System.out.println("\n3-2. 注文ステータスを更新");
        boolean updated = OrderService.updateOrderStatus(order.getOrderId(), "配送中");
        System.out.println("更新成功: " + updated);
        
        // テスト3: 注文IDで検索
        System.out.println("\n3-3. 注文IDで注文を検索");
        OrderService.Order foundOrder = OrderService.getOrderById(order.getOrderId());
        if (foundOrder != null) {
            System.out.println("検索結果 - ステータス: " + foundOrder.getStatus());
        }
        
        // テスト4: 顧客IDで注文一覧取得
        System.out.println("\n3-4. 顧客の注文履歴を取得");
        List<OrderService.Order> customerOrders = OrderService.getOrdersByCustomerId("customer_001");
        System.out.println("顧客customer_001の注文数: " + customerOrders.size());
    }
    
    /**
     * ユーティリティ機能のテスト
     */
    private static void testUtilityFunction() {
        System.out.println("\n[TEST 4] ユーティリティ機能\n");
        
        // テスト1: 文字列チェック
        System.out.println("4-1. 文字列チェック");
        System.out.println("isEmpty(\"\") = " + CommonUtils.isEmpty(""));
        System.out.println("isEmpty(\"test\") = " + CommonUtils.isEmpty("test"));
        System.out.println("isNotEmpty(\"\") = " + CommonUtils.isNotEmpty(""));
        System.out.println("isNotEmpty(\"test\") = " + CommonUtils.isNotEmpty("test"));
        
        // テスト2: 日付フォーマット
        System.out.println("\n4-2. 日付フォーマット");
        System.out.println("現在時刻: " + CommonUtils.getCurrentDateTime());
        
        // テスト3: 金額フォーマット
        System.out.println("\n4-3. 金額フォーマット");
        System.out.println("金額フォーマット(10000): " + CommonUtils.formatCurrency(10000));
        System.out.println("金額フォーマット(1234567): " + CommonUtils.formatCurrency(1234567));
        
        // テスト4: バリデーション
        System.out.println("\n4-4. バリデーション");
        System.out.println("isValidEmail(\"test@example.com\") = " + CommonUtils.isValidEmail("test@example.com"));
        System.out.println("isValidEmail(\"invalid-email\") = " + CommonUtils.isValidEmail("invalid-email"));
        System.out.println("isValidNumber(\"123\") = " + CommonUtils.isValidNumber("123"));
        System.out.println("isValidNumber(\"abc\") = " + CommonUtils.isValidNumber("abc"));
    }
    
    /**
     * ログインレスポンスを表示
     */
    private static void printLoginResponse(LoginResponse response) {
        System.out.println("成功: " + response.isSuccess());
        System.out.println("メッセージ: " + response.getMessage());
        if (response.isSuccess()) {
            System.out.println("ユーザー: " + response.getUsername());
            System.out.println("ロール: " + response.getRole());
            System.out.println("トークン: " + (response.getToken() != null ? response.getToken().substring(0, 20) + "..." : "null"));
        }
    }
}