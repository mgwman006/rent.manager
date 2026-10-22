package tz.tante.rent.manager.controllers.graphql;

import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import tz.tante.rent.manager.engines.rent.dtos.RentCollectionSummary;
import tz.tante.rent.manager.services.graphql.RentCollectionSubscriptionPublisher;

@Controller
@AllArgsConstructor
public class RentCollectionSubscriptionController
{
  private final RentCollectionSubscriptionPublisher publisher;

  @SubscriptionMapping
  public Flux<RentCollectionSummary> rentCollectionUpdates(@Argument Long leaseId)
  {
    System.out.println("Subscription requested for leaseId: " + leaseId);
    return publisher.subscribe(leaseId);
  }
}
