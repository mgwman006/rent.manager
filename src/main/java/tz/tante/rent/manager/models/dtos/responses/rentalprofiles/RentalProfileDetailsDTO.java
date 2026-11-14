package tz.tante.rent.manager.models.dtos.responses.rentalprofiles;

import tz.tante.rent.manager.enums.RentalProfileType;
import tz.tante.rent.manager.models.dtos.responses.RentReceivingAccountDetailsDTO;

import java.math.BigDecimal;

public record RentalProfileDetailsDTO(
  long id,
  String name,
  String phoneNumber,
  String email,
  Long userId,
  Long organizationId,
  BigDecimal viewingServiceFee,
  RentalProfileType type,
  RentReceivingAccountDetailsDTO rentReceivingAccount)
{
}
