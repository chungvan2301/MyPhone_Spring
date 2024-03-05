package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "ID")
    private User user;

    private String province;
    private String district;
    private String ward;
    private String phoneNumber;
    private String streetAndDepartment;
    private String type;
    private Boolean addressDefault;
    private String nameReceiver;

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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStreetAndDepartment() {
        return streetAndDepartment;
    }

    public void setStreetAndDepartment(String streetAndDepartment) {
        this.streetAndDepartment = streetAndDepartment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getAddressDefault() {
        return addressDefault;
    }

    public void setAddressDefault(Boolean addressDefault) {
        this.addressDefault = addressDefault;
    }

    public String getNameReceiver() {
        return nameReceiver;
    }

    public void setNameReceiver(String nameReceiver) {
        this.nameReceiver = nameReceiver;
    }

    public Address(Long id, User user, String province, String district, String ward, String phoneNumber, String streetAndDepartment, String type, Boolean addressDefault, String nameReceiver) {
        this.id = id;
        this.user = user;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.phoneNumber = phoneNumber;
        this.streetAndDepartment = streetAndDepartment;
        this.type = type;
        this.addressDefault = addressDefault;
        this.nameReceiver = nameReceiver;
    }

    public Address() {
    }
}
