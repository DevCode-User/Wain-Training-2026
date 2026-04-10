package com.example.system;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import com.example.system.admin.controller.*;
import com.example.system.admin.service.ProductService;
import com.example.system.customer.service.OrderService;
import com.example.system.common.AuthMannager;
import com.example.system.common.CommonUtils;

public class SimpleHttpServer {
    private static final int PORT = 8888;
    private static final String FRONTEND_PATH = "wain_kadai/frontend";

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // APIエンドポイント
        server.createContext("/api/login", exchange -> handleLogin(exchange));
        server.createContext("/api/logout", exchange -> handleLogout(exchange));
        server.createContext("/api/validate", exchange -> handleValidate(exchange));
        server.createContext("/api/products", exchange -> handleProducts(exchange));
        server.createContext("/api/orders", exchange -> handleOrders(exchange));
        server.createContext("/api/user", exchange -> handleUser(exchange));
        
        // フロントエンドサービング
        server.createContext("/", exchange -> handleStatic(exchange));
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("========================================");
        System.out.println("  HTTP Server Started");
        System.out.println("========================================");
        System.out.println("URL: http://localhost:" + PORT);
        System.out.println("ログイン画面: http://localhost:" + PORT + "/admin/login.html");
        System.out.println("顧客ページ: http://localhost:" + PORT + "/customer/index.html");
        System.out.println("========================================");
    }

    // CORSヘッダー追加
    private static void addCORSHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    // ログインAPI
    private static void handleLogin(HttpExchange exchange) throws IOException {
        addCORSHeaders(exchange);
        
        if (!exchange.getRequestMethod().equals("POST")) {
            send(exchange, 405, "{\"success\": false, \"message\": \"Method not allowed\"}");
            return;
        }
        
        try {
            String body = readBody(exchange);
            String username = "", password = "";
            
            // JSONパース（簡易版）
            if (body.contains("username")) {
                username = extractJsonValue(body, "username");
                password = extractJsonValue(body, "password");
            } else {
                // URLエンコード形式に対応
                String[] parts = body.split("&");
                for (String part : parts) {
                    if (part.startsWith("username=")) {
                        username = decode(part.substring(9));
                    } else if (part.startsWith("password=")) {
                        password = decode(part.substring(9));
                    }
                }
            }
            
            LoginResponse response = AuthMannager.login(username, password);
            String json = String.format(
                "{\"success\": %b, \"message\": \"%s\", \"token\": \"%s\", \"username\": \"%s\", \"role\": \"%s\"}",
                response.isSuccess(),
                response.getMessage(),
                response.getToken() != null ? response.getToken() : "",
                response.getUsername() != null ? response.getUsername() : "",
                response.getRole() != null ? response.getRole() : ""
            );
            send(exchange, 200, json);
        } catch (Exception e) {
            send(exchange, 500, "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }

    // ログアウトAPI
    private static void handleLogout(HttpExchange exchange) throws IOException {
        addCORSHeaders(exchange);
        
        try {
            String token = getQueryParam(exchange, "token");
            if (token != null && !token.isEmpty()) {
                AuthMannager.logout(token);
                send(exchange, 200, "{\"success\": true, \"message\": \"ログアウトしました\"}");
            } else {
                send(exchange, 400, "{\"success\": false, \"message\": \"トークンが必要です\"}");
            }
        } catch (Exception e) {
            send(exchange, 500, "{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }

    // トークン検証API
    private static void handleValidate(HttpExchange exchange) throws IOException {
        addCORSHeaders(exchange);
        
        try {
            String token = getQueryParam(exchange, "token");
            boolean valid = token != null && AuthMannager.validateToken(token);
            send(exchange, 200, "{\"valid\": " + valid + "}");
        } catch (Exception e) {
            send(exchange, 500, "{\"valid\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }

    // 商品API
    private static void handleProducts(HttpExchange exchange) throws IOException {
        addCORSHeaders(exchange);
        
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            
            if (method.equals("GET")) {
                String id = getQueryParam(exchange, "id");
                if (id != null && !id.isEmpty()) {
                    // 特定商品取得
                    var product = ProductService.getProductById(Integer.parseInt(id));
                    if (product != null) {
                        String json = String.format(
                            "{\"id\": %d, \"name\": \"%s\", \"price\": %d, \"stock\": %d, \"description\": \"%s\"}",
                            product.getId(), product.getName(), product.getPrice(), product.getStock(), product.getDescription()
                        );
                        send(exchange, 200, json);
                    } else {
                        send(exchange, 404, "{\"error\": \"Product not found\"}");
                    }
                } else {
                    // 全商品取得
                    var products = ProductService.getAllProducts();
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < products.size(); i++) {
                        var p = products.get(i);
                        json.append(String.format(
                            "{\"id\": %d, \"name\": \"%s\", \"price\": %d, \"stock\": %d}",
                            p.getId(), p.getName(), p.getPrice(), p.getStock()
                        ));
                        if (i < products.size() - 1) json.append(",");
                    }
                    json.append("]");
                    send(exchange, 200, json.toString());
                }
            }
        } catch (Exception e) {
            send(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // 注文API
    private static void handleOrders(HttpExchange exchange) throws IOException {
        addCORSHeaders(exchange);
        
        try {
            String method = exchange.getRequestMethod();
            
            if (method.equals("GET")) {
                String customerId = getQueryParam(exchange, "customerId");
                if (customerId != null && !customerId.isEmpty()) {
                    var orders = OrderService.getOrdersByCustomerId(customerId);
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < orders.size(); i++) {
                        var order = orders.get(i);
                        json.append(String.format(
                            "{\"orderId\": \"%s\", \"totalAmount\": %d, \"status\": \"%s\"}",
                            order.getOrderId(), order.getTotalAmount(), order.getStatus()
                        ));
                        if (i < orders.size() - 1) json.append(",");
                    }
                    json.append("]");
                    send(exchange, 200, json.toString());
                }
            }
        } catch (Exception e) {
            send(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // ユーザーAPI
    private static void handleUser(HttpExchange exchange) throws IOException {
        addCORSHeaders(exchange);
        
        try {
            String token = getQueryParam(exchange, "token");
            if (token != null && AuthMannager.validateToken(token)) {
                var user = AuthMannager.getUserByToken(token);
                if (user != null) {
                    String json = String.format(
                        "{\"userId\": \"%s\", \"username\": \"%s\", \"email\": \"%s\", \"role\": \"%s\"}",
                        user.getUserId(), user.getUsername(), user.getEmail(), user.getRole()
                    );
                    send(exchange, 200, json);
                } else {
                    send(exchange, 401, "{\"error\": \"User not found\"}");
                }
            } else {
                send(exchange, 401, "{\"error\": \"Invalid token\"}");
            }
        } catch (Exception e) {
            send(exchange, 500, "{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // 静的ファイルサービング
    private static void handleStatic(HttpExchange exchange) throws IOException {
        addCORSHeaders(exchange);
        
        try {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/") || path.isEmpty()) {
                path = "/admin/login.html";
            }
            
            File file = new File(FRONTEND_PATH + path);
            
            if (!file.exists() || file.isDirectory()) {
                send(exchange, 404, "<h1>404 - Not Found</h1>");
                return;
            }
            
            String contentType = getContentType(path);
            exchange.getResponseHeaders().set("Content-Type", contentType);
            
            byte[] fileData = Files.readAllBytes(file.toPath());
            exchange.sendResponseHeaders(200, fileData.length);
            exchange.getResponseBody().write(fileData);
            exchange.close();
        } catch (Exception e) {
            send(exchange, 500, "<h1>500 - Server Error</h1><p>" + e.getMessage() + "</p>");
        }
    }

    // ヘルパーメソッド
    private static void send(HttpExchange exchange, int code, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        byte[] data = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(code, data.length);
        exchange.getResponseBody().write(data);
        exchange.close();
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }

    private static String getQueryParam(HttpExchange exchange, String param) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) return null;
        
        for (String part : query.split("&")) {
            if (part.startsWith(param + "=")) {
                return decode(part.substring(param.length() + 1));
            }
        }
        return null;
    }

    private static String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start == -1) return "";
        start += pattern.length();
        int end = json.indexOf("\"", start);
        return end == -1 ? "" : json.substring(start, end);
    }

    private static String decode(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        } catch (Exception e) {
            return str;
        }
    }

    private static String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=utf-8";
        if (path.endsWith(".css")) return "text/css; charset=utf-8";
        if (path.endsWith(".js")) return "application/javascript; charset=utf-8";
        if (path.endsWith(".json")) return "application/json; charset=utf-8";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".gif")) return "image/gif";
        return "text/plain; charset=utf-8";
    }
}
