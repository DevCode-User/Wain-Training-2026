package com.example.ec.service;

import com.example.ec.dto.*;
import com.example.ec.entity.Admin;
import com.example.ec.entity.Customer;
import com.example.ec.repository.AdminRepository;
import com.example.ec.repository.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(CustomerRepository customerRepository, AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResult loginCustomer(String loginId, String rawPassword) {
        Optional<Customer> found = customerRepository.findByLoginId(loginId);
        if (found.isEmpty()) return new LoginResult(false, "CUSTOMER", null, null);
        Customer customer = found.get();
        if (!passwordEncoder.matches(rawPassword, customer.getPassword())) return new LoginResult(false, "CUSTOMER", null, null);
        return new LoginResult(true, "CUSTOMER", customer.getCustomerName(), customer.getCustomerId());
    }

    public LoginResult loginAdmin(String loginId, String rawPassword) {
        Optional<Admin> found = adminRepository.findByLoginId(loginId);
        if (found.isEmpty()) return new LoginResult(false, "ADMIN", null, null);
        Admin admin = found.get();
        if (!passwordEncoder.matches(rawPassword, admin.getPassword())) return new LoginResult(false, "ADMIN", null, null);
        return new LoginResult(true, "ADMIN", admin.getAdminName(), admin.getAdminId());
    }

    public RegisterResult registerCustomer(RegisterForm form) {
        if (customerRepository.existsByLoginId(form.getLoginId())) return new RegisterResult(false, "ログインIDはすでに使用されています。", null);
        if (customerRepository.existsByCustomerEmail(form.getCustomerEmail())) return new RegisterResult(false, "このメールアドレスは既に登録されています。", null);
        Customer customer = new Customer();
        customer.setLoginId(form.getLoginId());
        customer.setPassword(passwordEncoder.encode(form.getPassword()));
        customer.setCustomerName(form.getCustomerName());
        customer.setCustomerEmail(form.getCustomerEmail());
        customer.setPostalCode(form.getPostalCode());
        customer.setAddressLine1(form.getAddressLine1());
        customer.setAddressLine2(form.getAddressLine2());
        customer.setPhone(form.getPhone());
        Customer saved = customerRepository.save(customer);
        return new RegisterResult(true, "会員登録が完了しました。ログイン画面からログインしてください。", saved.getCustomerId());
    }

    public Customer getCustomer(Integer customerId) { return customerRepository.findById(customerId).orElse(null); }
}
