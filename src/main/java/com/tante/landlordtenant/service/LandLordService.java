package com.tante.landlordtenant.service;

import com.tante.landlordtenant.models.Entities.Landlord.Landlord;
import com.tante.landlordtenant.models.Entities.Tenants.Tenant;
import com.tante.landlordtenant.models.Entities.Users.User;
import com.tante.landlordtenant.models.Requests.LandLords.LandLordRequestDto;
import com.tante.landlordtenant.models.Requests.Tenants.TenantRequestDto;
import com.tante.landlordtenant.models.Responses.LandLords.LandLordResponseDto;
import com.tante.landlordtenant.models.Responses.LandLords.LandLordWithTenants;
import com.tante.landlordtenant.models.Responses.Tenants.TenantResponseDto;
import com.tante.landlordtenant.repository.LandlordRepository;
import com.tante.landlordtenant.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public TenantResponseDto registerTenant(Long landlordId, TenantRequestDto requestDto) {
        Optional<Landlord> optionalLandlord = landlordRepository.findById(landlordId);
        if (optionalLandlord.isEmpty())
            throw new EntityNotFoundException("LandLord with Given Id Does not exist");

        Landlord landlord = optionalLandlord.get();
        Tenant newTenant = new Tenant(requestDto.firstName(), requestDto.lastName(), requestDto.phoneNumber(), requestDto.email());
        newTenant.setLandlord(landlord);
        landlord.addTenant(newTenant);

        landlord = landlordRepository.save(landlord);

        landlord = getLandlordWithTenant(landlord.getId());

        Optional<Tenant> optionalTenant= landlord.getTenants()
                .stream()
                .filter(t -> t.getEmail().equals(requestDto.email())).findFirst();

        if (optionalTenant.isEmpty())
            throw  new EntityNotFoundException("Tenant not created");

        newTenant = optionalTenant.get();
        return new TenantResponseDto(
                newTenant.getId(),
                newTenant.getFirstName(),
                newTenant.getLastName(),
                newTenant.getPhoneNumber(),
                newTenant.getEmail());
    }

    @Transactional
    public Landlord getLandlordWithTenant(Long landlordId) {
        return landlordRepository.findByIdWithOptionalTenant(landlordId)
                .orElseThrow(() -> new EntityNotFoundException("Landlord not found"));
    }

    public LandLordWithTenants getLandlordWithTenants(Long landLordId) {
        Landlord landlord = getLandlordWithTenant(landLordId);
        return new LandLordWithTenants(
                new LandLordResponseDto(
                        landlord.getId(),
                        landlord.getFirstName(),
                        landlord.getLastName(),
                        landlord.getPhoneNumber(),
                        landlord.getEmail()
                ),
                landlord.getTenants()
                        .stream()
                        .map(t -> new TenantResponseDto(
                                t.getId(),
                                t.getFirstName(),
                                t.getLastName(),
                                t.getPhoneNumber(),
                                t.getEmail()
                        )).toList()
        );
    }
}
