package tz.tante.rent.manager.services.graphql;


import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import tz.tante.rent.manager.engines.rent.dtos.RentCollectionSummary;

@Component
public class RentCollectionSubscriptionPublisher
{
  private final Sinks.Many<RentCollectionSummary> sink = Sinks.many().multicast().onBackpressureBuffer();

  public void publish(RentCollectionSummary summary)
  {
    sink.tryEmitNext(summary);
  }

  public Flux<RentCollectionSummary> subscribe(Long leaseId)
  {
    return sink.asFlux()
      .filter(summary ->
        summary.leaseId().equals(leaseId));
  }
}

