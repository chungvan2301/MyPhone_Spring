package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepo extends JpaRepository <Product, Long> {
    List<Product> findByCategoryId (Long id);
    List<Product> findDistinctTop6ByProductStatusOrderByAddDateDesc(String productStatus);

    List<Product> findDistinctTop6ByQuantitySoldGreaterThanEqualOrderByAddDateDesc(int quantity);

    @Query("SELECT p.color FROM Product p WHERE p.name = :name GROUP BY p.color")
    List<String> findDistinctColorByName(String name);
    @Query("SELECT p.type FROM Product p WHERE p.name = :name GROUP BY p.type")
    List<String> findDistinctTypeByName(String name);

    @Query(value = "SELECT * FROM (SELECT p.*, ROW_NUMBER() OVER (PARTITION BY p.name ORDER BY p.id) as row_number_alias FROM product p WHERE p.cate_id = :id AND p.branch IN :branches AND p.price BETWEEN :minPrice AND :maxPrice AND p.product_status IN :productStatuses) as subquery WHERE subquery.row_number_alias = 1", nativeQuery = true)
    List<Product> findDistinctNameByCategoryIdAndBranchInAndPriceBetweenAndProductStatusIn(Long id, Set<String> branches, int minPrice, int maxPrice, Set<String> productStatuses);

    @Query(value = "SELECT * FROM (SELECT p.*, ROW_NUMBER() OVER (PARTITION BY p.name ORDER BY p.id) as row_number_alias FROM product p WHERE p.cate_id = :id AND p.branch IN :branches AND p.price BETWEEN :minPrice AND :maxPrice AND p.product_status IN :productStatuses) as subquery WHERE subquery.row_number_alias = 1", nativeQuery = true)
    List<Product> findDistinctNameByCategoryIdAndBranchInAndPriceBetweenAndProductStatusIn(Long id, Set<String> branches, int minPrice, int maxPrice, Set<String> productStatuses, Pageable pageable);


    @Query("SELECT p.branch FROM Product p WHERE p.category.id = :id GROUP BY p.branch")
    Set<String> findDistinctBranchByCategoryId(Long id);
    Product findByNameAndTypeAndColor(String name, String type, String color);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.id IN (SELECT MIN(p2.id) FROM Product p2 WHERE LOWER(p2.name) LIKE LOWER(CONCAT('%', :keyword, '%')) GROUP BY p2.name)")
    List<Product> findDistinctProductsByNameContainingIgnoreCase(String keyword);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.category.id = :id AND p.id IN (SELECT MIN(p2.id) FROM Product p2 WHERE LOWER(p2.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p2.category.id = :id GROUP BY p2.name)")
    List<Product> findDistinctProductsByNameContainingIgnoreCaseAndCategory_Id(String keyword, Long id);


}
