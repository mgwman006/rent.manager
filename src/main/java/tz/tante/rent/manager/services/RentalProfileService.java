package tz.tante.rent.manager.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import tz.tante.rent.manager.enums.RentalProfileType;
import tz.tante.rent.manager.exceptions.TanteException;
import tz.tante.rent.manager.models.dtos.requests.rentalprofiles.CreateRentalProfileDTO;
import tz.tante.rent.manager.models.dtos.responses.MembershipDetailsDTO;
import tz.tante.rent.manager.models.dtos.responses.rentalprofiles.RentalProfileDetailsDTO;
import tz.tante.rent.manager.models.entities.Membership;
import tz.tante.rent.manager.models.entities.RentalProfile;
import tz.tante.rent.manager.repositories.RentalProfileRepository;

import java.util.ArrayList;

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
      RentalProfile rentalProfile = new RentalProfile();
      rentalProfile.setName(createRentalProfileDTO.name());
      rentalProfile.setBusinessEmail(createRentalProfileDTO.businessEmail());
      rentalProfile.setType(createRentalProfileDTO.type());

      rentalProfile  = rentalProfileRepository.save(rentalProfile);

      Membership membership = new Membership();
      membership.setUserId(createRentalProfileDTO.adminUserId());
      rentalProfile.addMembership(membership);

      rentalProfile = rentalProfileRepository.save(rentalProfile);

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
          )).toList()
      );
    }
    catch (Exception exception)
    {
      throw new TanteException(exception.getMessage());
    }
  }
}
