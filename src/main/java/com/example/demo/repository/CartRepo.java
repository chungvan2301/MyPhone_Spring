package com.example.demo.repository;

import com.example.demo.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepo extends JpaRepository <Cart, Long> {
    List<Cart> findByUserIdAndSold (Long userId, int sold);
    Cart findByUserIdAndProductId (Long userId, Long productId);
    Cart findByUserIdAndProductIdAndSold (Long userId, Long productId, int sold);
}
