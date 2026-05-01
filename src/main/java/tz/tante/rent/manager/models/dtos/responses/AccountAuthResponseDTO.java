package tz.tante.rent.manager.models.dtos.responses;

public record AccountAuthResponseDTO(
  String  accountId,
  String jwtToken
) { }
