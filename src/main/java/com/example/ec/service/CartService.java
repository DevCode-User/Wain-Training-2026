package com.example.ec.service;

import com.example.ec.dto.CartItem;
import com.example.ec.entity.Product;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private static final String CART_SESSION_KEY = "SHOPPING_CART";

    public void addToCart(Product product, Integer quantity, HttpSession session) {
        List<CartItem> cart = getCart(session);
        
        // 既に同じ商品がカートに入っているか確認
        boolean found = false;
        for (CartItem item : cart) {
            if (item.getProductId().equals(product.getProductId())) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }

        // 新規商品の場合はカートに追加
        if (!found) {
            CartItem newItem = new CartItem(
                product.getProductId(),
                product.getProductName(),
                product.getPrice(),
                quantity,
                product.getCategory()
            );
            cart.add(newItem);
        }

        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void removeFromCart(Integer productId, HttpSession session) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(item -> item.getProductId().equals(productId));
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void updateQuantity(Integer productId, Integer quantity, HttpSession session) {
        List<CartItem> cart = getCart(session);
        for (CartItem item : cart) {
            if (item.getProductId().equals(productId)) {
                if (quantity <= 0) {
                    removeFromCart(productId, session);
                } else {
                    item.setQuantity(quantity);
                    session.setAttribute(CART_SESSION_KEY, cart);
                }
                break;
            }
        }
    }

    public List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
        }
        return cart;
    }

    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }

    public Integer getCartTotal(HttpSession session) {
        return getCart(session).stream()
            .mapToInt(CartItem::getSubtotal)
            .sum();
    }

    public Integer getCartItemCount(HttpSession session) {
        return getCart(session).stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
    }
}
