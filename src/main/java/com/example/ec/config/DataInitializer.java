package com.example.ec.config;

import com.example.ec.entity.Admin;
import com.example.ec.entity.Customer;
import com.example.ec.entity.Product;
import com.example.ec.repository.AdminRepository;
import com.example.ec.repository.CustomerRepository;
import com.example.ec.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CustomerRepository customerRepository,
                           AdminRepository adminRepository,
                           ProductRepository productRepository,
                           PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (customerRepository.count() == 0) {
            Customer customer = new Customer();
            customer.setLoginId("user001");
            customer.setPassword(passwordEncoder.encode("cust1234"));
            customer.setCustomerName("サンプル顧客");
            customer.setCustomerEmail("user001@example.com");
            customer.setPostalCode("123-4567");
            customer.setAddressLine1("東京都千代田区1-1-1");
            customer.setAddressLine2("サンプルビル101");
            customer.setPhone("090-1111-2222");
            customerRepository.save(customer);
        }

        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setLoginId("admin001");
            admin.setPassword(passwordEncoder.encode("admin1234"));
            admin.setAdminName("サンプル管理者");
            admin.setPermissionLevel(1);
            adminRepository.save(admin);
        }

        if (productRepository.count() == 0) {
            Product p1 = new Product();
            p1.setProductName("ミネラルウォーター箱（24本入り）");
            p1.setDescription("冷たい飲み物をたっぷり備蓄できる24本入りのミネラルウォーター。");
            p1.setPrice(1980);
            p1.setStockQuantity(60);
            p1.setCategory("飲料・食品");
            p1.setSize("500ml × 24本");
            p1.setWeight("12kg");
            p1.setOrigin("日本");
            p1.setIsActive(true);
            productRepository.save(p1);

            Product p2 = new Product();
            p2.setProductName("キャラクターフィギュア");
            p2.setDescription("人気キャラクターのフィギュア。コレクションにぴったりの一品です。");
            p2.setPrice(4200);
            p2.setStockQuantity(25);
            p2.setCategory("ホビー");
            p2.setSize("高さ約20cm");
            p2.setWeight("300g");
            p2.setOrigin("中国");
            p2.setIsActive(true);
            productRepository.save(p2);

            Product p3 = new Product();
            p3.setProductName("家庭用アクションゲームソフト");
            p3.setDescription("家族みんなで楽しめる人気のアクションゲームソフト。");
            p3.setPrice(7200);
            p3.setStockQuantity(40);
            p3.setCategory("ゲーム");
            p3.setSize("標準ケース");
            p3.setWeight("150g");
            p3.setOrigin("日本");
            p3.setIsActive(true);
            productRepository.save(p3);

            Product p4 = new Product();
            p4.setProductName("防災セット（一人用）");
            p4.setDescription("非常時に役立つ飲料・保存食・ライトなどが入った防災セット。");
            p4.setPrice(4980);
            p4.setStockQuantity(30);
            p4.setCategory("日用品");
            p4.setSize("横30cm×奥20cm×高40cm");
            p4.setWeight("2.5kg");
            p4.setOrigin("日本");
            p4.setIsActive(true);
            productRepository.save(p4);

            Product p5 = new Product();
            p5.setProductName("キッチンペーパー（12ロール）");
            p5.setDescription("厚手で吸水性の高いキッチンペーパー。日常使いに最適です。");
            p5.setPrice(960);
            p5.setStockQuantity(80);
            p5.setCategory("日用品");
            p5.setSize("シングル 12ロール");
            p5.setWeight("1.2kg");
            p5.setOrigin("日本");
            p5.setIsActive(true);
            productRepository.save(p5);

            Product p6 = new Product();
            p6.setProductName("洗濯洗剤（大容量）");
            p6.setDescription("家族の洗濯に安心の大容量タイプ。しっかり汚れを落とします。");
            p6.setPrice(1380);
            p6.setStockQuantity(50);
            p6.setCategory("日用品");
            p6.setSize("1500g");
            p6.setWeight("1.6kg");
            p6.setOrigin("日本");
            p6.setIsActive(true);
            productRepository.save(p6);

            Product p7 = new Product();
            p7.setProductName("ふわふわぬいぐるみ");
            p7.setDescription("抱き心地の良いふわふわぬいぐるみ。ギフトにもおすすめです。");
            p7.setPrice(2600);
            p7.setStockQuantity(35);
            p7.setCategory("ギフト");
            p7.setSize("高さ約30cm");
            p7.setWeight("250g");
            p7.setOrigin("中国");
            p7.setIsActive(true);
            productRepository.save(p7);

            Product p8 = new Product();
            p8.setProductName("LEDデスクライト");
            p8.setDescription("調光機能付きのLEDデスクライト。読書や作業に便利です。");
            p8.setPrice(3280);
            p8.setStockQuantity(45);
            p8.setCategory("生活雑貨");
            p8.setSize("ベース直径約20cm、高さ約50cm");
            p8.setWeight("700g");
            p8.setOrigin("台湾");
            p8.setIsActive(true);
            productRepository.save(p8);
        }
    }
}
