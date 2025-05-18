package com.tante.landlordtenant.repository;

import com.tante.landlordtenant.models.Entities.Photo;

import java.util.List;

public interface PhotoRepository
{
    List<Photo> findByHouseId(String houseId);
    List<Photo> findByApartmentId(String apartmentId);

    void deleteByHouseId(String houseId);
    void deleteByApartmentId(String apartmentId);
}
