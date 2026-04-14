package com.example.ec.config;

import com.example.ec.entity.*;
import com.example.ec.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CustomerRepository customerRepository, AdminRepository adminRepository, CategoryRepository categoryRepository, ProductRepository productRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository; this.adminRepository = adminRepository; this.categoryRepository = categoryRepository; this.productRepository = productRepository; this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (customerRepository.count() == 0) {
            Customer customer = new Customer();
            customer.setLoginId("user001"); customer.setPassword(passwordEncoder.encode("cust1234")); customer.setCustomerName("サンプル顧客"); customer.setCustomerEmail("user001@example.com"); customer.setPostalCode("123-4567"); customer.setAddressLine1("東京都千代田区1-1-1"); customer.setAddressLine2("サンプルビル101"); customer.setPhone("090-1111-2222");
            customerRepository.save(customer);
        }
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setLoginId("admin001"); admin.setPassword(passwordEncoder.encode("admin1234")); admin.setAdminName("サンプル管理者"); admin.setPermissionLevel(1);
            adminRepository.save(admin);
        }
        ensureCategory("飲料・食品"); ensureCategory("ホビー"); ensureCategory("ゲーム"); ensureCategory("日用品"); ensureCategory("生活雑貨"); ensureCategory("ギフト");
        if (productRepository.count() == 0) {
            addProduct("ミネラルウォーター箱（24本入り）", "冷たい飲み物をたっぷり備蓄できる24本入りのミネラルウォーター。", 1980, 60, "飲料・食品", "500ml × 24本", "12kg", "日本");
            addProduct("キャラクターフィギュア", "人気キャラクターのフィギュア。コレクションにぴったりの一品です。", 4200, 25, "ホビー", "高さ約20cm", "300g", "中国");
            addProduct("家庭用アクションゲームソフト", "家族みんなで楽しめる人気のアクションゲームソフト。", 7200, 40, "ゲーム", "標準ケース", "150g", "日本");
            addProduct("防災セット（一人用）", "非常時に役立つ飲料・保存食・ライトなどが入った防災セット。", 4980, 30, "日用品", "横30cm×奥20cm×高40cm", "2.5kg", "日本");
            addProduct("キッチンペーパー（12ロール）", "厚手で吸水性の高いキッチンペーパー。日常使いに最適です。", 960, 80, "日用品", "シングル 12ロール", "1.2kg", "日本");
            addProduct("洗濯洗剤（大容量）", "家族の洗濯に安心の大容量タイプ。しっかり汚れを落とします。", 1380, 50, "日用品", "1500g", "1.6kg", "日本");
            addProduct("ふわふわぬいぐるみ", "抱き心地の良いふわふわぬいぐるみ。ギフトにもおすすめです。", 2600, 35, "ギフト", "高さ約30cm", "250g", "中国");
            addProduct("LEDデスクライト", "調光機能付きのLEDデスクライト。読書や作業に便利です。", 3280, 45, "生活雑貨", "ベース直径約20cm、高さ約50cm", "700g", "台湾");
        }
    }

    private void createCategory(String name) { Category c = new Category(); c.setCategoryName(name); categoryRepository.save(c); }
    private void ensureCategory(String name) { if (categoryRepository.findByCategoryName(name).isEmpty()) createCategory(name); }
    private void addProduct(String name, String description, int price, int stock, String categoryName, String size, String weight, String origin) {
        Category category = categoryRepository.findByCategoryName(categoryName).orElseThrow();
        Product p = new Product();
        p.setCategoryId(category.getCategoryId()); p.setCategoryEntity(category); p.setProductName(name); p.setDescription(description); p.setPrice(price); p.setStock(stock); p.setIsActive(true); p.setSize(size); p.setWeight(weight); p.setOrigin(origin);
        productRepository.save(p);
    }
}
