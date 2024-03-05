package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "ID")
    private User user;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false, referencedColumnName = "ID")
    private Product product;
    private int quantity;
    private int sold;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public Cart(Long id, User user, Product product, int quantity, int sold) {
        this.id = id;
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.sold = sold;
    }

    public Cart() {
    }
}
