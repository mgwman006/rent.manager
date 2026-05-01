package tz.tante.rent.manager.models.dtos.responses;


public record AccountDetailsDto(
  Long id,
  String phone,
  String email,
  boolean enabled
) { }
