package tz.tante.rent.manager.services.graphql;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tz.tante.rent.manager.engines.rent.dtos.RentCollectionSummary;
import tz.tante.rent.manager.services.LeaseService;

@Component
@RequiredArgsConstructor
public class RentPaymentRecordedEventListener {

  private final LeaseService leaseService;
  private final RentCollectionSubscriptionPublisher publisher;
  private final CacheManager cacheManager;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onPaymentRecorded(RentPaymentRecordedEvent event)
  {
    Cache cache = cacheManager.getCache("rent-summary");
    if (cache != null)
    {
      cache.evict(event.leaseId());
    }

    RentCollectionSummary summary = leaseService.getLeasePaymentStatus(event.leaseId());
    publisher.publish(summary);
  }
}
