package com.example.ec.config;

import com.example.ec.entity.Admin;
import com.example.ec.entity.Customer;
import com.example.ec.repository.AdminRepository;
import com.example.ec.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CustomerRepository customerRepository,
                           AdminRepository adminRepository,
                           PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
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
    }
}
