package tz.tante.rent.manager.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import java.util.ArrayList;


import tz.tante.rent.manager.enums.MembershipRole;
import tz.tante.rent.manager.exceptions.ResourceExistException;
import tz.tante.rent.manager.exceptions.TanteException;
import tz.tante.rent.manager.models.dtos.requests.rentalprofiles.CreateRentalProfileDTO;
import tz.tante.rent.manager.models.dtos.responses.MembershipDetailsDTO;
import tz.tante.rent.manager.models.dtos.responses.RentReceivingAccountDetailsDTO;
import tz.tante.rent.manager.models.dtos.responses.rentalprofiles.RentalProfileDetailsDTO;
import tz.tante.rent.manager.models.entities.Membership;
import tz.tante.rent.manager.models.entities.RentReceivingAccount;
import tz.tante.rent.manager.models.entities.RentalProfile;
import tz.tante.rent.manager.repositories.MembershipRepository;
import tz.tante.rent.manager.repositories.RentalProfileRepository;


@Service
@AllArgsConstructor
@Setter
@Getter
public class RentalProfileService
{
  private final MembershipRepository membershipRepository;
  private final RentalProfileRepository rentalProfileRepository;

  @Transactional
  public RentalProfileDetailsDTO createRentalProfile(CreateRentalProfileDTO createRentalProfileDTO)
  {
    try
    {
      if (membershipRepository.isExistByUserIdAndMembershipRole(createRentalProfileDTO.adminUserId(), MembershipRole.OWNER))
      {
        throw new ResourceExistException("User with ID " + createRentalProfileDTO.adminUserId() + " already owns a rental profile.");
      }

      RentalProfile rentalProfile = rentalProfileRepository.findByName(createRentalProfileDTO.name());
      if (rentalProfile == null)
      {
        rentalProfile = new RentalProfile();
        rentalProfile.setName(createRentalProfileDTO.name());
        rentalProfile.setBusinessEmail(createRentalProfileDTO.businessEmail());
        rentalProfile.setType(createRentalProfileDTO.type());

        rentalProfile  = rentalProfileRepository.save(rentalProfile);

        Membership membership = new Membership();
        membership.setUserId(createRentalProfileDTO.adminUserId());
        rentalProfile.addMembership(membership);
        membership.setMembershipRole(MembershipRole.OWNER);

        rentalProfile = rentalProfileRepository.save(rentalProfile);

        if (createRentalProfileDTO.rentReceivingAccount() != null)
        {
          RentReceivingAccount rentReceivingAccount = getRentReceivingAccount(createRentalProfileDTO);
          rentalProfile.addRentReceivingAccount(rentReceivingAccount);
          rentalProfile = rentalProfileRepository.save(rentalProfile);
        }
      }



      RentReceivingAccount rentReceivingAccount = null;
      if (rentalProfile.getRentReceivingAccounts() != null)
      {
        rentReceivingAccount = rentalProfile.getRentReceivingAccounts()
          .stream()
          .filter(RentReceivingAccount::isDefault)
          .findFirst()
          .orElse(null);
      }

      RentalProfile finalRentalProfile = rentalProfile;

      return new RentalProfileDetailsDTO(
        rentalProfile.getId(),
        rentalProfile.getName(),
        rentalProfile.getBusinessEmail(),
        rentalProfile.getType(),
        rentalProfile.getMemberships() == null ? new ArrayList<>() :
          rentalProfile.getMemberships()
            .stream()
            .map(m -> new MembershipDetailsDTO(
                m.getId(),
                m.getUserId(),
                finalRentalProfile.getId(),
                finalRentalProfile.getName()
          )).toList(),
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
    catch (Exception exception)
    {
      throw new TanteException(exception.getMessage());
    }
  }

  private static RentReceivingAccount getRentReceivingAccount(CreateRentalProfileDTO createRentalProfileDTO)
  {
    RentReceivingAccount rentReceivingAccount = new RentReceivingAccount();
    rentReceivingAccount.setAccountNumber(createRentalProfileDTO.rentReceivingAccount().accountNumber());
    rentReceivingAccount.setBankName(createRentalProfileDTO.rentReceivingAccount().bankName());
    rentReceivingAccount.setMobileMoneyProvider(createRentalProfileDTO.rentReceivingAccount().mobileMoneyProvider());
    rentReceivingAccount.setMobileMoneyNumber(createRentalProfileDTO.rentReceivingAccount().mobileMoneyNumber());
    rentReceivingAccount.setDefault(createRentalProfileDTO.rentReceivingAccount().isDefault());
    rentReceivingAccount.setPaymentMethod(createRentalProfileDTO.rentReceivingAccount().paymentMethod());
    return rentReceivingAccount;
  }
}
