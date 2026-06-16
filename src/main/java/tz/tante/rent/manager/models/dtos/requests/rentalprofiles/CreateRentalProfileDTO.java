package tz.tante.rent.manager.models.dtos.requests.rentalprofiles;

import tz.tante.rent.manager.enums.RentalProfileType;

public record CreateRentalProfileDTO(
  Long adminUserId,
  String name,
  String businessEmail,
  RentalProfileType type)
{
}
