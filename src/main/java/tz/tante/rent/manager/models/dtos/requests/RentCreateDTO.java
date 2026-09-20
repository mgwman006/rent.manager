package tz.tante.rent.manager.models.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tz.tante.rent.manager.enums.RentFrequency;

import java.math.BigDecimal;

public record RentCreateDTO(
    Long id,
    @NotNull(message = "Amount cannot be null")
    BigDecimal amount,
    @NotBlank(message = "Currency cannot be blank")
    String currency,
    @NotBlank(message = "Frequency cannot be blank")
    RentFrequency frequency
)
{
}
