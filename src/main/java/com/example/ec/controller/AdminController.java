package com.example.ec.controller;

import com.example.ec.dto.ProductForm;
import com.example.ec.entity.Order;
import com.example.ec.entity.Product;
import com.example.ec.service.AdminService;
import com.example.ec.service.OrderService;
import com.example.ec.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    private final ProductService productService;
    private final OrderService orderService;
    public AdminController(AdminService adminService, ProductService productService, OrderService orderService) { this.adminService = adminService; this.productService = productService; this.orderService = orderService; }
    @ModelAttribute("productForm") public ProductForm productForm() { return new ProductForm(); }
    private boolean isAdmin(HttpSession session) { return "ADMIN".equals(session.getAttribute("LOGIN_ROLE")); }

    @GetMapping("/home")
    public String adminHome(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        model.addAttribute("userName", session.getAttribute("LOGIN_USER_NAME"));
        model.addAttribute("dashboard", adminService.getDashboardSummary());
        return "admin-home";
    }

    @GetMapping("/products")
    public String productManagement(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        model.addAttribute("products", productService.getAllProductsForAdmin());
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("userName", session.getAttribute("LOGIN_USER_NAME"));
        return "admin-products";
    }

    @GetMapping("/products/new")
    public String newProduct(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("pageTitle", "商品新規登録");
        return "admin-product-form";
    }

    @PostMapping("/products")
    public String createProduct(HttpSession session, @Valid @ModelAttribute("productForm") ProductForm form, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        if (bindingResult.hasErrors()) { model.addAttribute("categories", productService.getAllCategories()); model.addAttribute("pageTitle", "商品新規登録"); return "admin-product-form"; }
        try { productService.saveProduct(form); redirectAttributes.addFlashAttribute("successMessage", "商品を登録しました。"); return "redirect:/admin/products"; }
        catch (IllegalArgumentException ex) { model.addAttribute("errorMessage", ex.getMessage()); model.addAttribute("categories", productService.getAllCategories()); model.addAttribute("pageTitle", "商品新規登録"); return "admin-product-form"; }
        catch (Exception ex) { model.addAttribute("errorMessage", "商品登録に失敗しました: " + ex.getMessage()); model.addAttribute("categories", productService.getAllCategories()); model.addAttribute("pageTitle", "商品新規登録"); return "admin-product-form"; }
    }

    @GetMapping("/products/{productId}/edit")
    public String editProduct(@PathVariable Integer productId, HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        Product product = productService.getProductById(productId);
        if (product == null) return "redirect:/admin/products";
        model.addAttribute("productForm", productService.toForm(product));
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("pageTitle", "商品編集");
        return "admin-product-form";
    }

    @PostMapping("/products/{productId}")
    public String updateProduct(@PathVariable Integer productId, HttpSession session, @Valid @ModelAttribute("productForm") ProductForm form, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        if (bindingResult.hasErrors()) { model.addAttribute("categories", productService.getAllCategories()); model.addAttribute("pageTitle", "商品編集"); return "admin-product-form"; }
        try { productService.updateProduct(productId, form); redirectAttributes.addFlashAttribute("successMessage", "商品を更新しました。"); return "redirect:/admin/products"; }
        catch (IllegalArgumentException ex) { model.addAttribute("errorMessage", ex.getMessage()); model.addAttribute("categories", productService.getAllCategories()); model.addAttribute("pageTitle", "商品編集"); return "admin-product-form"; }
        catch (Exception ex) { model.addAttribute("errorMessage", "商品更新に失敗しました: " + ex.getMessage()); model.addAttribute("categories", productService.getAllCategories()); model.addAttribute("pageTitle", "商品編集"); return "admin-product-form"; }
    }


    @PostMapping("/products/save")
    public String saveProduct(HttpSession session, @Valid @ModelAttribute("productForm") ProductForm form, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        if (bindingResult.hasErrors()) { model.addAttribute("categories", productService.getAllCategories()); model.addAttribute("pageTitle", form.getProductId() == null ? "商品新規登録" : "商品編集"); return "admin-product-form"; }
        try {
            if (form.getProductId() == null) { productService.saveProduct(form); redirectAttributes.addFlashAttribute("successMessage", "商品を登録しました。"); }
            else { productService.updateProduct(form.getProductId(), form); redirectAttributes.addFlashAttribute("successMessage", "商品を更新しました。"); }
            return "redirect:/admin/products";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage()); model.addAttribute("categories", productService.getAllCategories()); model.addAttribute("pageTitle", form.getProductId() == null ? "商品新規登録" : "商品編集"); return "admin-product-form";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", (form.getProductId() == null ? "商品登録" : "商品更新") + "に失敗しました: " + ex.getMessage()); model.addAttribute("categories", productService.getAllCategories()); model.addAttribute("pageTitle", form.getProductId() == null ? "商品新規登録" : "商品編集"); return "admin-product-form";
        }
    }

    @PostMapping("/products/{productId}/delete")
    public String deleteProduct(@PathVariable Integer productId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        productService.softDeleteProduct(productId); redirectAttributes.addFlashAttribute("successMessage", "商品を非表示にしました。");
        return "redirect:/admin/products";
    }

    @GetMapping("/stocks")
    public String stockManagement(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        model.addAttribute("products", productService.getAllProductsForAdmin());
        model.addAttribute("userName", session.getAttribute("LOGIN_USER_NAME"));
        return "admin-stocks";
    }

    @PostMapping("/stocks/bulk-update")
    public String bulkUpdateStock(HttpSession session, @RequestParam("productId") List<Integer> productIds, @RequestParam("stock") List<Integer> stocks, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        Map<Integer, Integer> stockMap = new LinkedHashMap<>();
        for (int i = 0; i < productIds.size(); i++) stockMap.put(productIds.get(i), stocks.get(i));
        productService.bulkUpdateStocks(stockMap); redirectAttributes.addFlashAttribute("successMessage", "在庫を一括更新しました。");
        return "redirect:/admin/stocks";
    }

    @PostMapping("/stocks/upload")
    public String uploadStockCsv(HttpSession session, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        try { Map<Integer, Integer> stockMap = adminService.parseStockCsv(file); productService.bulkUpdateStocks(stockMap); redirectAttributes.addFlashAttribute("successMessage", "CSVから在庫を更新しました。"); }
        catch (IOException | IllegalArgumentException ex) { redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage()); }
        return "redirect:/admin/stocks";
    }

    @GetMapping("/orders")
    public String orderManagement(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("userName", session.getAttribute("LOGIN_USER_NAME"));
        return "admin-orders";
    }

    @GetMapping("/orders/{orderId}")
    public String orderDetail(@PathVariable Integer orderId, HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        Order order = orderService.getOrderById(orderId); if (order == null) return "redirect:/admin/orders";
        model.addAttribute("order", order); model.addAttribute("orderItems", orderService.getOrderItems(orderId)); model.addAttribute("userName", session.getAttribute("LOGIN_USER_NAME"));
        return "admin-order-detail";
    }

    @PostMapping("/orders/{orderId}/status")
    public String updateOrderStatus(@PathVariable Integer orderId, @RequestParam String orderStatus, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) return "redirect:/admin/login";
        orderService.updateOrderStatus(orderId, orderStatus); redirectAttributes.addFlashAttribute("successMessage", "注文状態を更新しました。");
        return "redirect:/admin/orders/" + orderId;
    }
}
