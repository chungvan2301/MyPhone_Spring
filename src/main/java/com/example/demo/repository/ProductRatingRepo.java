package com.example.demo.repository;

import com.example.demo.model.ProductRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRatingRepo extends JpaRepository <ProductRating, Long> {
    List<ProductRating> findAllByProductIdOrderByDateTimeDesc(Long productId);

    ProductRating findByProductIdAndUserId(Long productId, Long userId);
    void deleteByProductIdAndUserId(Long productId, Long userId);
}
