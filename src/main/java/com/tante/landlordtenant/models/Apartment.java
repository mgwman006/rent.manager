package com.tante.landlordtenant.models;

import javax.persistence.*;
import java.util.List;

public class Apartment
{
    @Id
    @Column(name = "apartment_id")
    String apartmentId;

    @Column(name="rent")
    int rent;

    List<Photo> photoList;

    House house;

    Contract contract;

    String status;

    int bedRoom;

    int masterRoom;

    String sittingRoom;

    String publicToilet;

    @Column(name = "kitchen")
    String kitchen;

    @Column(name = "furniture")
    String furniture;

    @Column(name = "electricity")
    String electricity;

    @Column(name = "water")
    String water;

    @Column(name = "parking")
    String parking;

    @Column(name = "more_details")
    String more_details;

    public Apartment() {
    }

    public Apartment(String apartmentId, int rent, House house, String status, int bedRoom, int masterRoom, String sittingRoom, String publicToilet, String kitchen, String furniture, String electricity, String water, String parking, String more_details) {
        this.apartmentId = apartmentId;
        this.rent = rent;
        this.house = house;
        this.status = status;
        this.bedRoom = bedRoom;
        this.masterRoom = masterRoom;
        this.sittingRoom = sittingRoom;
        this.publicToilet = publicToilet;
        this.kitchen = kitchen;
        this.furniture = furniture;
        this.electricity = electricity;
        this.water = water;
        this.parking = parking;
        this.more_details = more_details;
    }

    public String getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(String apartmentId) {
        this.apartmentId = apartmentId;
    }

    public int getRent() {
        return rent;
    }

    public void setRent(int rent) {
        this.rent = rent;
    }

    public List<Photo> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<Photo> photoList) {
        this.photoList = photoList;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getBedRoom() {
        return bedRoom;
    }

    public void setBedRoom(int bedRoom) {
        this.bedRoom = bedRoom;
    }

    public int getMasterRoom() {
        return masterRoom;
    }

    public void setMasterRoom(int masterRoom) {
        this.masterRoom = masterRoom;
    }

    public String getSittingRoom() {
        return sittingRoom;
    }

    public void setSittingRoom(String sittingRoom) {
        this.sittingRoom = sittingRoom;
    }

    public String getPublicToilet() {
        return publicToilet;
    }

    public void setPublicToilet(String publicToilet) {
        this.publicToilet = publicToilet;
    }

    public String getKitchen() {
        return kitchen;
    }

    public void setKitchen(String kitchen) {
        this.kitchen = kitchen;
    }

    public String getFurniture() {
        return furniture;
    }

    public void setFurniture(String furniture) {
        this.furniture = furniture;
    }

    public String getElectricity() {
        return electricity;
    }

    public void setElectricity(String electricity) {
        this.electricity = electricity;
    }

    public String getWater() {
        return water;
    }

    public void setWater(String water) {
        this.water = water;
    }

    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public String getMore_details() {
        return more_details;
    }

    public void setMore_details(String more_details) {
        this.more_details = more_details;
    }

    @Override
    public String toString() {
        return "Apartment{" +
                "apartmentId='" + apartmentId + '\'' +
                ", rent=" + rent +
                ", photoList=" + photoList +
                ", house=" + house +
                ", contract=" + contract +
                ", status='" + status + '\'' +
                ", bedRoom=" + bedRoom +
                ", masterRoom=" + masterRoom +
                ", sittingRoom='" + sittingRoom + '\'' +
                ", publicToilet='" + publicToilet + '\'' +
                ", kitchen='" + kitchen + '\'' +
                ", furniture='" + furniture + '\'' +
                ", electricity='" + electricity + '\'' +
                ", water='" + water + '\'' +
                ", parking='" + parking + '\'' +
                ", more_details='" + more_details + '\'' +
                '}';
    }
}
