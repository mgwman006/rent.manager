package tz.tante.rent.manager.models.dtos.requests.leases;

import jakarta.validation.constraints.NotNull;

public record LeaseTermsUpdateDTO(
  @NotNull(message = "Lease terms cannot be null") String leaseTerms
)
{
}