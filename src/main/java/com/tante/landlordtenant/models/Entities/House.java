package com.tante.landlordtenant.models.Entities;

import com.tante.landlordtenant.models.Entities.Landlord.Landlord;
import jakarta.persistence.Column;
import jakarta.persistence.Id;

import java.util.List;

public class House
{
    @Id
    @Column(name = "house_id")
    String houseId;

    List<Photo> photoList;

    List<Apartment> apartments;

    MailingAddress address;

    Landlord landlord;

    public House() {
    }

    public House(String houseId, Landlord landlord) {
        this.houseId = houseId;
        this.landlord = landlord;
    }

    public String getHouseId() {
        return houseId;
    }

    public void setHouseId(String houseId) {
        this.houseId = houseId;
    }

    public List<Photo> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<Photo> photoList) {
        this.photoList = photoList;
    }

    public List<Apartment> getApartments() {
        return apartments;
    }

    public void setApartments(List<Apartment> apartments) {
        this.apartments = apartments;
    }

    public MailingAddress getAddress() {
        return address;
    }

    public void setAddress(MailingAddress address) {
        this.address = address;
    }

    public Landlord getLandlord() {
        return landlord;
    }

    public void setLandlord(Landlord landlord) {
        this.landlord = landlord;
    }
}
