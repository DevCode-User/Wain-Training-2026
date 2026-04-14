package com.example.ec.service;

import com.example.ec.dto.CartItem;
import com.example.ec.entity.Cart;
import com.example.ec.entity.CartItemEntity;
import com.example.ec.entity.Product;
import com.example.ec.repository.CartItemEntityRepository;
import com.example.ec.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemEntityRepository cartItemEntityRepository;
    public CartService(CartRepository cartRepository, CartItemEntityRepository cartItemEntityRepository) {
        this.cartRepository = cartRepository;
        this.cartItemEntityRepository = cartItemEntityRepository;
    }

    @Transactional
    public void addToCart(Integer customerId, Product product, Integer quantity) {
        validateQuantity(quantity);
        if (product == null || !product.isActive()) throw new IllegalArgumentException("該当する商品は見つかりません。");
        if (product.getStock() == null || product.getStock() <= 0) throw new IllegalArgumentException("在庫が存在しないためカートに追加できません。");
        Cart cart = getOrCreateCart(customerId);
        CartItemEntity entity = cartItemEntityRepository.findByCartIdAndProductId(cart.getCartId(), product.getProductId()).orElseGet(() -> {
            CartItemEntity e = new CartItemEntity(); e.setCartId(cart.getCartId()); e.setProductId(product.getProductId()); e.setProduct(product); e.setQuantity(0); return e; });
        int newQty = entity.getQuantity() + quantity;
        if (newQty > 10) throw new IllegalArgumentException("一度に購入できる数量は最大10個までです。");
        if (newQty > product.getStock()) throw new IllegalArgumentException("在庫数を超える数量は指定できません。");
        entity.setQuantity(newQty);
        cartItemEntityRepository.save(entity);
        touch(cart);
    }

    @Transactional
    public void removeFromCart(Integer customerId, Integer productId) {
        Cart cart = getOrCreateCart(customerId);
        cartItemEntityRepository.findByCartIdAndProductId(cart.getCartId(), productId).ifPresent(cartItemEntityRepository::delete);
        touch(cart);
    }

    @Transactional
    public void updateQuantity(Integer customerId, Integer productId, Integer quantity) {
        Cart cart = getOrCreateCart(customerId);
        CartItemEntity entity = cartItemEntityRepository.findByCartIdAndProductId(cart.getCartId(), productId).orElseThrow(() -> new IllegalArgumentException("カート明細が見つかりません。"));
        if (quantity == null || quantity <= 0) {
            cartItemEntityRepository.delete(entity);
        } else {
            validateQuantity(quantity);
            Product product = entity.getProduct();
            if (product != null && quantity > product.getStock()) throw new IllegalArgumentException("在庫数を超える数量は指定できません。");
            entity.setQuantity(quantity);
            cartItemEntityRepository.save(entity);
        }
        touch(cart);
    }

    public List<CartItem> getCart(Integer customerId) {
        Cart cart = getOrCreateCart(customerId);
        List<CartItem> items = new ArrayList<>();
        for (CartItemEntity entity : cartItemEntityRepository.findByCartId(cart.getCartId())) {
            Product product = entity.getProduct();
            if (product == null) continue;
            items.add(new CartItem(product.getProductId(), product.getProductName(), product.getPrice(), entity.getQuantity(), product.getCategoryName()));
        }
        return items;
    }

    @Transactional
    public void clearCart(Integer customerId) {
        Cart cart = getOrCreateCart(customerId);
        cartItemEntityRepository.deleteByCartId(cart.getCartId());
        touch(cart);
    }

    public Integer getCartTotal(Integer customerId) { return getCart(customerId).stream().mapToInt(CartItem::getSubtotal).sum(); }
    public Cart getOrCreateCart(Integer customerId) { return cartRepository.findByCustomerId(customerId).orElseGet(() -> { Cart c = new Cart(); c.setCustomerId(customerId); c.setCreatedAt(LocalDateTime.now()); c.setUpdatedAt(LocalDateTime.now()); return cartRepository.save(c); }); }
    private void validateQuantity(Integer q) { if (q == null || q < 1) throw new IllegalArgumentException("数量は1以上で入力してください。"); if (q > 10) throw new IllegalArgumentException("一度に購入できる数量は最大10個までです。"); }
    private void touch(Cart cart) { cart.setUpdatedAt(LocalDateTime.now()); cartRepository.save(cart); }
}
