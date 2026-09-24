package tz.tante.rent.manager.engines.rent;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
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
  private final LeaseRepository leaseRepository;

  public RentCollectionSummary getLeasePaymentStatus(Long leaseId)
  {
    Lease lease = leaseRepository.findById(leaseId)
      .orElseThrow(() -> new TanteException("Lease not found for id: " + leaseId));

//    if (!isValidOperationalLease(lease)) {
//      throw new TanteException("Lease is not valid for payment status calculation.");
//    }

    List<PaymentBlock> paymentBlocks = lease.getPaymentBlocks();
    List<PaymentBlockSummary> paymentBlockSummaries = getPaymentBlockSummaries(paymentBlocks);

    return getRentCollectionSummary(paymentBlockSummaries);
  }

  private boolean isValidOperationalLease(Lease lease)
  {
    if (lease.getStatus() != LeaseStatus.ACTIVE) {
      return false;
    }

    if (lease.getEndDate().isBefore(lease.getStartDate())) {
      return false;
    }

    return !lease.getEndDate().isBefore(LocalDate.now());
  }

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

  private List<PaymentBlockSummary> getPaymentBlockSummaries(List<PaymentBlock> paymentBlocks)
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

  private RentCollectionSummary getRentCollectionSummary(List<PaymentBlockSummary> paymentBlockSummaries)
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

}
