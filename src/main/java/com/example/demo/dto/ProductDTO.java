package com.example.demo.dto;

public class ProductDTO {
    private Long id;
    private String name;
    private Long categoryId;
    private double price;
    private String branch;
    private String type;
    private String color;
    private String description;
    private int quantityAdd;
    private String imageName;

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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


    public int getQuantityAdd() {
        return quantityAdd;
    }

    public void setQuantityAdd(int quantityAdd) {
        this.quantityAdd = quantityAdd;
    }

    public ProductDTO(Long id, String name, Long categoryId, double price, String branch, String type, String color, String description, int quantityAdd, String imageName) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
        this.branch = branch;
        this.type = type;
        this.color = color;
        this.description = description;
        this.quantityAdd = quantityAdd;
        this.imageName = imageName;
    }
    public ProductDTO() {

    }
}
