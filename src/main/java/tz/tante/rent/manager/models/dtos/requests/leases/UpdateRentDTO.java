package tz.tante.rent.manager.models.dtos.requests.leases;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import tz.tante.rent.manager.models.dtos.requests.RentCreateDTO;

public record UpdateRentDTO(
  @NotNull(message = "Rent details cannot be null") @Valid RentCreateDTO rent,
  boolean fullLeasePaymentRequired
)
{
}
