package com.tante.landlordtenant.service;

import com.tante.landlordtenant.models.Photo;
import com.tante.landlordtenant.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class PhotoService
{
    @Autowired
    PhotoRepository photoRepository;

//    //CREATE
//    public Photo createPhoto(Photo photo)
//    {
//        return photoRepository.save(photo);
//    }
//
//    //GETS
//    public List<Photo> getPhotos ()
//    {
//        return photoRepository.findAll();
//    }
//
//    public Optional<Photo> getPhotoById(String photoId)
//    {
//        return photoRepository.findById(photoId);
//    }
//
//    public List<Photo> findByHouseId(String houseId)
//    {
//        return photoRepository.findByHouseId(houseId);
//    }
//    public List<Photo> findByApartmentId(String apartmentId)
//    {
//        return photoRepository.findByApartmentId(apartmentId);
//    }
//
//    //DELETE
//    public void deletePhotoById(String photoId)
//    {
//        photoRepository.deleteById(photoId);
//    }
//
//    public void deletePhotoByHouseId(String houseId)
//    {
//        photoRepository.deleteByHouseId(houseId);
//    }
//
//    public void deleteByApartmentId(String apartmentId)
//    {
//        photoRepository.deleteByApartmentId(apartmentId);
//    }
//
//    //Update
//
//    public Photo updatePhoto (String photoId, Photo photoDetails)
//    {
//        Photo photo = photoRepository.findById(photoId).get();
//        photo.setApartment(photoDetails.getApartment());
//        photo.setHouse(photoDetails.getHouse());
//        photo.setPhotoPath(photoDetails.getPhotoPath());
//        return photoRepository.save(photo);
//    }






}
