package com.example.demo.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cate_id", referencedColumnName="cate_id")
    private Category category;
    private double price;
    private String branch;
    @Column(nullable = true)
    @ColumnDefault("null")
    private String type;
    @Column(nullable = true)
    @ColumnDefault("null")
    private String color;
    private String description;
    private String imageName;
    private LocalDateTime addDate;
    @Column(columnDefinition = "VARCHAR(255) default 'new'")
    private String productStatus;
    private int quantityAdd;
    @Column(columnDefinition = "int default 0")
    private int quantitySold;
    private int quantityInStore;
    private double avgRating;
    public Product(Long id, String name, Category category, double price, String branch, String type, String color, String description, String imageName, LocalDateTime addDate, String productStatus, int quantityAdd, int quantitySold, int quantityInStore, double avgRating) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.branch = branch;
        this.type = type;
        this.color = color;
        this.description = description;
        this.imageName = imageName;
        this.addDate = addDate;
        this.productStatus = productStatus;
        this.quantityAdd = quantityAdd;
        this.quantitySold = quantitySold;
        this.quantityInStore = quantityInStore;
        this.avgRating = avgRating;
    }

    public Product () {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public LocalDateTime getAddDate() {
        return addDate;
    }

    public void setAddDate(LocalDateTime addDate) {
        this.addDate = addDate;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public int getQuantityAdd() {
        return quantityAdd;
    }

    public void setQuantityAdd(int quantityAdd) {
        this.quantityAdd = quantityAdd;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public int getQuantityInStore() {
        return quantityInStore;
    }

    public void setQuantityInStore(int quantityInStore) {
        this.quantityInStore = quantityInStore;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

}
