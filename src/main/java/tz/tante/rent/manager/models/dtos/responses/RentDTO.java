package tz.tante.rent.manager.models.dtos.responses;

import java.math.BigDecimal;

public record RentDTO(
    Long id,
    BigDecimal amount,
    String currency,
    String frequency
)
{
}
