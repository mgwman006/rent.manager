package tz.tante.rent.manager.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


import tz.tante.rent.manager.exceptions.ResourceExistException;
import tz.tante.rent.manager.exceptions.ResourceNotFoundException;
import tz.tante.rent.manager.exceptions.TanteException;
import tz.tante.rent.manager.models.dtos.requests.rentalprofiles.CreateRentalProfileDTO;
import tz.tante.rent.manager.models.dtos.responses.RentReceivingAccountDetailsDTO;
import tz.tante.rent.manager.models.dtos.responses.rentalprofiles.RentalProfileDetailsDTO;
import tz.tante.rent.manager.models.entities.RentReceivingAccount;
import tz.tante.rent.manager.models.entities.RentalProfile;
import tz.tante.rent.manager.repositories.RentalProfileRepository;


@Service
@AllArgsConstructor
@Setter
@Getter
public class RentalProfileService
{
  private final RentalProfileRepository rentalProfileRepository;

  @Transactional
  public RentalProfileDetailsDTO createRentalProfile(CreateRentalProfileDTO createRentalProfileDTO)
  {
    try
    {
      Optional<RentalProfile> rentalProfileOptional = rentalProfileRepository.findByPhoneNumber(createRentalProfileDTO.phoneNumber());
      if (rentalProfileOptional.isPresent())
      {
        throw new ResourceExistException("Rental profile with phone number " + createRentalProfileDTO.phoneNumber() + " already exists.");
      }

      RentalProfile rentalProfile = new RentalProfile();
      rentalProfile.setUserId(createRentalProfileDTO.userId());
      rentalProfile.setOrganizationId(createRentalProfileDTO.organizationId());
      rentalProfile.setPhoneNumber(createRentalProfileDTO.phoneNumber());
      rentalProfile.setEmail(createRentalProfileDTO.email());
      rentalProfile.setName(createRentalProfileDTO.name());
      rentalProfile.setType(createRentalProfileDTO.type());
      rentalProfile  = rentalProfileRepository.save(rentalProfile);

      return getRentalProfileDetailsDTO(rentalProfile);
    }
    catch (Exception exception)
    {
      throw new TanteException(exception.getMessage());
    }
  }

  public RentalProfileDetailsDTO getRentalProfile(Long rentalProfileId)
  {
    RentalProfile rentalProfile = rentalProfileRepository.findById(rentalProfileId)
      .orElseThrow(() -> new ResourceNotFoundException("Rental profile with ID " + rentalProfileId + " not found."));
    return getRentalProfileDetailsDTO(rentalProfile);
  }

  public List<RentalProfileDetailsDTO> getAllRentalProfilesByUserIdOrOrganizationId(Long userId, Long organizationId)
  {
    List<RentalProfile> rentalProfiles = rentalProfileRepository.findByUserIdOrOrganizationId(userId, organizationId);
    return rentalProfiles.stream()
      .map(RentalProfileService::getRentalProfileDetailsDTO)
      .toList();
  }

  private static RentalProfileDetailsDTO getRentalProfileDetailsDTO(RentalProfile rentalProfile)
  {
    RentReceivingAccount rentReceivingAccount = null;
    if (rentalProfile.getRentReceivingAccounts() != null)
    {
      rentReceivingAccount = rentalProfile.getRentReceivingAccounts()
        .stream()
        .filter(RentReceivingAccount::isDefault)
        .findFirst()
        .orElse(null);
    }

    return new RentalProfileDetailsDTO(
      rentalProfile.getId(),
      rentalProfile.getName(),
      rentalProfile.getPhoneNumber(),
      rentalProfile.getEmail(),
      rentalProfile.getUserId(),
      rentalProfile.getOrganizationId(),
      rentalProfile.getType(),
      rentReceivingAccount == null ? null : new RentReceivingAccountDetailsDTO(
        rentReceivingAccount.getId(),
        rentReceivingAccount.getAccountNumber(),
        rentReceivingAccount.getBankName(),
        rentReceivingAccount.getMobileMoneyNumber(),
        rentReceivingAccount.getMobileMoneyProvider(),
        rentReceivingAccount.getPaymentMethod(),
        rentReceivingAccount.isDefault())
    );
  }
}
