package tz.tante.rent.manager.models.dtos.responses.rentalprofiles;

import tz.tante.rent.manager.enums.RentalProfileType;
import tz.tante.rent.manager.models.dtos.responses.MembershipDetailsDTO;
import tz.tante.rent.manager.models.dtos.responses.RentReceivingAccountDetailsDTO;

import java.util.List;

public record RentalProfileDetailsDTO(
  long id,
  String name,
  String businessEmail,
  RentalProfileType type,
  RentReceivingAccountDetailsDTO rentReceivingAccount)
{
}
