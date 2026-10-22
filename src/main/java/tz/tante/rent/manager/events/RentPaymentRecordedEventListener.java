package tz.tante.rent.manager.events;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tz.tante.rent.manager.engines.rent.dtos.RentCollectionSummary;
import tz.tante.rent.manager.services.LeaseService;
import tz.tante.rent.manager.services.graphql.RentCollectionSubscriptionPublisher;

@Component
@RequiredArgsConstructor
public class RentPaymentRecordedEventListener {

  private final LeaseService leaseService;
  private final RentCollectionSubscriptionPublisher publisher;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onPaymentRecorded(RentPaymentRecordedEvent event) {

    RentCollectionSummary summary = leaseService.getLeasePaymentStatus(event.leaseId());

    publisher.publish(summary);
  }
}
