package com.example.demo.dto;

public class AddressDTO {
    private Long id;
    private Long userId;
    private String nameReceiver;
    private String province;
    private String district;
    private String ward;
    private String provinceId;
    private String districtId;
    private String wardId;
    private String streetAndDepartment;
    private String phoneNumber;
    private String type;
    private Boolean addressDefault;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNameReceiver() {
        return nameReceiver;
    }

    public void setNameReceiver(String nameReceiver) {
        this.nameReceiver = nameReceiver;
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

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getWardId() {
        return wardId;
    }

    public void setWardId(String wardId) {
        this.wardId = wardId;
    }

    public String getStreetAndDepartment() {
        return streetAndDepartment;
    }

    public void setStreetAndDepartment(String streetAndDepartment) {
        this.streetAndDepartment = streetAndDepartment;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public AddressDTO(Long id, Long userId, String nameReceiver, String province, String district, String ward, String provinceId, String districtId, String wardId, String streetAndDepartment, String phoneNumber, String type, Boolean addressDefault) {
        this.id = id;
        this.userId = userId;
        this.nameReceiver = nameReceiver;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.provinceId = provinceId;
        this.districtId = districtId;
        this.wardId = wardId;
        this.streetAndDepartment = streetAndDepartment;
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.addressDefault = addressDefault;
    }

    public AddressDTO() {
    }
}
