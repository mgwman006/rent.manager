package com.tante.landlordtenant.models;

import javax.persistence.*;


public class MailingAddress
{
    @Id
    @Column(name = "address_id")
    String addressId;

    @Column(name="county")
    String country;

    @Column(name="region")
    String region;

    @Column(name="city")
    String city;

    @Column(name = "street_name")
    String streetName;

    @Column(name="house_number")
    String houseNumber;

    @Column(name = "post_code")
    String postCode;

    House house;

    public MailingAddress() {
    }

    public MailingAddress(String addressId, String country, String region, String city, String streetName, String houseNumber, String postCode, House house) {
        this.addressId = addressId;
        this.country = country;
        this.region = region;
        this.city = city;
        this.streetName = streetName;
        this.houseNumber = houseNumber;
        this.postCode = postCode;
        this.house = house;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }
}
