package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dateCreated;
    private LocalDateTime dateCreatedDateFormat;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "ID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = false, referencedColumnName = "id")
    private Address address;

    @ElementCollection
    private List<Cart> cartsList = new ArrayList<Cart>();

    private double goodsFee;

    private double transportFee;

    private double totalPrice;

    private String receiptCode;

    private String paymentMethod;

    private String dayReceived;

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateCreatedDateFormat() {
        return dateCreatedDateFormat;
    }

    public void setDateCreatedDateFormat(LocalDateTime dateCreatedDateFormat) {
        this.dateCreatedDateFormat = dateCreatedDateFormat;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Cart> getCartsList() {
        return cartsList;
    }

    public void setCartsList(List<Cart> cartsList) {
        this.cartsList = cartsList;
    }

    public double getGoodsFee() {
        return goodsFee;
    }

    public void setGoodsFee(double goodsFee) {
        this.goodsFee = goodsFee;
    }

    public double getTransportFee() {
        return transportFee;
    }

    public void setTransportFee(double transportFee) {
        this.transportFee = transportFee;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getReceiptCode() {
        return receiptCode;
    }

    public void setReceiptCode(String receiptCode) {
        this.receiptCode = receiptCode;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDayReceived() {
        return dayReceived;
    }

    public void setDayReceived(String dayReceived) {
        this.dayReceived = dayReceived;
    }

    public Receipt(Long id, String dateCreated, LocalDateTime dateCreatedDateFormat, User user, Address address, List<Cart> cartsList, double goodsFee, double transportFee, double totalPrice, String receiptCode, String paymentMethod, String dayReceived, String status) {
        this.id = id;
        this.dateCreated = dateCreated;
        this.dateCreatedDateFormat = dateCreatedDateFormat;
        this.user = user;
        this.address = address;
        this.cartsList = cartsList;
        this.goodsFee = goodsFee;
        this.transportFee = transportFee;
        this.totalPrice = totalPrice;
        this.receiptCode = receiptCode;
        this.paymentMethod = paymentMethod;
        this.dayReceived = dayReceived;
        this.status = status;
    }

    public Receipt() {
    }
}
