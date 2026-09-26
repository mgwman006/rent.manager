package tz.tante.rent.manager.engines.rent;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import tz.tante.rent.manager.engines.rent.dtos.MonthlyCollectionSummary;
import tz.tante.rent.manager.engines.rent.dtos.PaymentBlockSummary;
import tz.tante.rent.manager.engines.rent.dtos.RentCollectionSummary;
import tz.tante.rent.manager.enums.*;
import tz.tante.rent.manager.exceptions.TanteException;
import tz.tante.rent.manager.models.entities.Lease;
import tz.tante.rent.manager.models.entities.PaymentBlock;
import tz.tante.rent.manager.models.entities.PaymentTransaction;
import tz.tante.rent.manager.models.entities.Rent;
import tz.tante.rent.manager.repositories.LeaseRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Setter
@AllArgsConstructor
public class RentCalculator
{

  private BigDecimal totalLeaseAmount(List<PaymentBlockSummary> paymentBlockSummaries)
  {
    BigDecimal total = BigDecimal.ZERO;
    for (PaymentBlockSummary summary : paymentBlockSummaries) {
      total = total.add(summary.amount());
    }
    return total;
  }

  private BigDecimal totalPayment(List<PaymentTransaction> transactions) {
    return transactions.stream()
      .filter(transaction -> transaction.getStatus() == PaymentStatus.SUCCESSFUL)
      .map(PaymentTransaction::getAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private RentCollectionStatus determineRentCollectionStatus(BigDecimal amountPaid, BigDecimal totalLeaseAmount)
  {
    if (amountPaid.compareTo(totalLeaseAmount) == 0) {
      return RentCollectionStatus.PAID;
    }  else if (amountPaid.compareTo(BigDecimal.ZERO) > 0) {
      return RentCollectionStatus.PARTIALLY_PAID;
    } else {
      return RentCollectionStatus.UNPAID;
    }
  }

  private PaymentBlockStatus determinePaymentBlockStatus(BigDecimal amountPaid, BigDecimal totalLeaseAmount)
  {
    if (amountPaid.compareTo(totalLeaseAmount) == 0) {
      return PaymentBlockStatus.PAID;
    }
    else {
      return PaymentBlockStatus.UNPAID;
    }
  }

  public List<PaymentBlockSummary> getPaymentBlockSummaries(List<PaymentBlock> paymentBlocks)
  {
    return paymentBlocks
      .stream()
      .map(this::mapPaymentBlockToSummary)
      .toList();
  }

  private PaymentBlockSummary mapPaymentBlockToSummary(PaymentBlock paymentBlock)
  {
    List<PaymentTransaction> transactions = paymentBlock.getTransactions();
    BigDecimal paidAmount = totalPayment(transactions);
    BigDecimal outstandingAmount = paymentBlock.getAmount().subtract(paidAmount);
    PaymentBlockStatus status = determinePaymentBlockStatus(paidAmount, paymentBlock.getAmount());
    return new PaymentBlockSummary(
      paymentBlock.getId(),
      paymentBlock.getAmount(),
      paidAmount,
      outstandingAmount,
      paymentBlock.getStartDate(),
      paymentBlock.getEndDate(),
      paymentBlock.getDueDate(),
      status
    );
  }

  public RentCollectionSummary getRentCollectionSummary(List<PaymentBlockSummary> paymentBlockSummaries)
  {
    BigDecimal totalLeaseAmount = totalLeaseAmount(paymentBlockSummaries);

    BigDecimal totalPaidAmount = paymentBlockSummaries.stream()
      .map(PaymentBlockSummary::paidAmount)
      .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal totalOutstandingAmount = totalLeaseAmount.subtract(totalPaidAmount);

    RentCollectionStatus overallPaymentStatus = determineRentCollectionStatus(totalPaidAmount, totalLeaseAmount);

    return new RentCollectionSummary(
      totalLeaseAmount,
      totalPaidAmount,
      totalOutstandingAmount,
      overallPaymentStatus,
      paymentBlockSummaries
    );
  }

  public static List<PaymentBlock> generatePaymentBlocksForLease(Lease lease) {
    List<PaymentBlock> paymentBlocks = new ArrayList<>();

    LocalDate currentStartDate = lease.getStartDate();
    LocalDate leaseEndDate = lease.getEndDate();
    Rent rent = lease.getRent();

    while (currentStartDate.isBefore(leaseEndDate)) {

      LocalDate currentEndDate = calculateEndDate(
        currentStartDate,
        rent.getFrequency(),
        leaseEndDate
      );

      PaymentBlock paymentBlock = new PaymentBlock();
      paymentBlock.setLease(lease);
      paymentBlock.setAmount(rent.getAmount());
      paymentBlock.setStartDate(currentStartDate);
      paymentBlock.setEndDate(currentEndDate);
      paymentBlock.setDueDate(currentEndDate);

      paymentBlocks.add(paymentBlock);

      currentStartDate = currentEndDate.plusDays(1);
    }

    return paymentBlocks;
  }

  private static LocalDate calculateEndDate(LocalDate startDate, RentFrequency rentFrequency, LocalDate leaseEndDate) {
    LocalDate endDate;
    switch (rentFrequency) {
      case DAILY -> endDate = startDate.plusDays(1).minusDays(1);
      case WEEKLY -> endDate = startDate.plusWeeks(1).minusDays(1);
      case MONTHLY -> endDate = startDate.plusMonths(1).minusDays(1);
      case YEARLY -> endDate = startDate.plusYears(1).minusDays(1);
      default -> throw new IllegalArgumentException("Unsupported rent frequency: " + rentFrequency);
    }
    return endDate.isAfter(leaseEndDate) ? leaseEndDate : endDate;
  }

  public MonthlyCollectionSummary getMonthlyCollectionSummary(List<PaymentBlock> paymentBlocks, int month, int year)
  {
    BigDecimal totalExpectedAmount = BigDecimal.ZERO;
    BigDecimal totalAmountPaid = BigDecimal.ZERO;

    for (PaymentBlock paymentBlock : paymentBlocks)
    {
      if ((paymentBlock.getStartDate().getYear() == year && paymentBlock.getStartDate().getMonthValue() == month) ||
        (paymentBlock.getEndDate().getYear() == year && paymentBlock.getEndDate().getMonthValue() == month))
      {
        totalExpectedAmount = totalExpectedAmount.add(paymentBlock.getAmount());
        totalAmountPaid = totalAmountPaid.add(totalPayment(paymentBlock.getTransactions()));
      }
    }

    BigDecimal totalOutstandingAmount = totalExpectedAmount.subtract(totalAmountPaid);
    double progressPercentage = totalExpectedAmount.compareTo(BigDecimal.ZERO) > 0
      ? totalAmountPaid.divide(totalExpectedAmount, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
      : 0.0;
    return new MonthlyCollectionSummary(totalExpectedAmount, totalAmountPaid, totalOutstandingAmount, progressPercentage);
  }
}
