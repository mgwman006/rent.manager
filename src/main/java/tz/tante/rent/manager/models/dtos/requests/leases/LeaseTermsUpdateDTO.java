package tz.tante.rent.manager.models.dtos.requests.leases;

import tz.tante.rent.manager.models.dtos.requests.RentCreateDTO;

import java.time.LocalDate;

public record LeaseTermsUpdateDTO(
  LocalDate startDate,
  LocalDate endDate,
  RentCreateDTO rent,
  boolean fullLeasePaymentRequired
)
{
}