package com.tante.landlordtenant.service;

import com.tante.landlordtenant.models.Entities.Landlord.Landlord;
import com.tante.landlordtenant.models.Entities.Tenants.Tenant;
import com.tante.landlordtenant.models.Entities.Users.User;
import com.tante.landlordtenant.models.Requests.LandLords.LandLordRequestDto;
import com.tante.landlordtenant.models.Responses.LandLords.LandLordResponseDto;
import com.tante.landlordtenant.repository.LandlordRepository;
import com.tante.landlordtenant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LandLordService {

    private final LandlordRepository landlordRepository;

    public LandLordService(LandlordRepository landlordRepository) {
        this.landlordRepository = landlordRepository;
    }

    public LandLordResponseDto registerLandLord(LandLordRequestDto requestDto)
    {
        Landlord newLandLord = new Landlord(
                requestDto.firstName(),
                requestDto.lastName(),
                requestDto.phoneNumber(),
                requestDto.email()
        );

        User newUser = new User(requestDto.email(), requestDto.passWord());

        newLandLord.setUser(newUser);
        newUser.setLandlord(newLandLord);

        newLandLord = landlordRepository.save(newLandLord);

        return new LandLordResponseDto(
                newLandLord.getId(),
                newLandLord.getFirstName(),
                newLandLord.getLastName(),
                newLandLord.getPhoneNumber(),
                newUser.getEmail()
        );

    }
}
