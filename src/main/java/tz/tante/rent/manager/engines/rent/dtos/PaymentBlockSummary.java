package tz.tante.rent.manager.engines.rent.dtos;

import tz.tante.rent.manager.enums.PaymentBlockStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentBlockSummary(
  Long id,
  BigDecimal amount,
  BigDecimal paidAmount,
  BigDecimal outstandingAmount,
  LocalDate startDate,
  LocalDate endDate,
  LocalDate dueDate,
  PaymentBlockStatus status
) {}
