package com.tante.landlordtenant.models.Entities;


import jakarta.persistence.Column;
import jakarta.persistence.Id;

public class Photo
{
    @Id
    @Column(name ="photo_id")
    String photoId;

    @Column(name = "photo_path")
    String photoPath;

    Apartment apartment;

    Apartment house;

    public Photo() {
    }

    public Photo(String photoId, String photoPath, Apartment apartment, Apartment house) {
        this.photoId = photoId;
        this.photoPath = photoPath;
        this.apartment = apartment;
        this.house = house;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }

    public Apartment getHouse() {
        return house;
    }

    public void setHouse(Apartment house) {
        this.house = house;
    }


}
