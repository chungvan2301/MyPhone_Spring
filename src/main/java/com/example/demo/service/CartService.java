package com.example.demo.service;

import com.example.demo.model.Cart;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.CartRepo;
import com.example.demo.repository.ProductRepo;
import com.example.demo.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    @Autowired
    UserRepo userRepo;

    @Autowired
    ProductRepo productRepo;

    @Autowired
    CartRepo cartRepo;

    public void addProductToCart (Long userId, Long productId, int quantity) {
        User user = userRepo.findById(userId).orElse(null);
        Product product = productRepo.findById(productId).orElse(null);
        this.updateCartSold(userId);
        Cart cart = new Cart();

        if(cartRepo.findByUserIdAndProductIdAndSold(userId,productId,0)!=null) {
            cart = cartRepo.findByUserIdAndProductIdAndSold(userId,productId,0);
            cart.setQuantity(cart.getQuantity()+quantity);
        } else {
            cart.setUser(user);
            cart.setProduct(product);
            cart.setQuantity(quantity);
            cart.setSold(0);
        }
        cartRepo.save(cart);
    }

    public List<Cart> viewCart (Long userId) {
        return cartRepo.findByUserIdAndSold(userId,0);
    }

    public void updateCartSold (Long userId) {
        List<Cart> carts = cartRepo.findByUserIdAndSold(userId,1);
        if (!carts.isEmpty()) {
            for (Cart cart : carts) {
                cart.setSold(0);
                cartRepo.save(cart);
            }
        }
    }
    public List<Cart> viewCartSold (Long userId) {
        return cartRepo.findByUserIdAndSold(userId,1);
    }
    public Cart findCartById (Long id) {
        return cartRepo.findById(id).orElse(null);
    }
    public double sumPriceProductsInCart (Long userId) {
        List<Cart> carts = cartRepo.findByUserIdAndSold(userId,1);
        return carts.stream().mapToDouble(cart->cart.getProduct().getPrice()*cart.getQuantity()).sum();
    }
    public void saveCart(Cart cart) {
        cartRepo.save(cart);
    }
    public void deleteProductInCart(Long id) { cartRepo.deleteById(id); }
    public void updateSoldCart (Long cartId) {
        Cart cart = cartRepo.findById(cartId).orElse(null);
        cart.setSold(1);
        cartRepo.save(cart);
    }
    public void updateNotSoldCart (Long cartId) {
        Cart cart = cartRepo.findById(cartId).orElse(null);
        cart.setSold(0);
        cartRepo.save(cart);
    }
    public int getCartSize (Long userId) {
        List<Cart> carts = cartRepo.findByUserIdAndSold(userId,0);
        if (carts.isEmpty()) return 0;
        return carts.size();
    }
}
