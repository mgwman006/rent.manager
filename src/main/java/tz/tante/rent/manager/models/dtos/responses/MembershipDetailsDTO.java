package tz.tante.rent.manager.models.dtos.responses;


public record MembershipDetailsDTO(
  Long id,
  Long userId,
  Long rentalProfileId,
  String rentalProfileName)
{}
