package tz.tante.rent.manager.controllers.graphql;


import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import tz.tante.rent.manager.engines.rent.dtos.RentCollectionSummary;
import tz.tante.rent.manager.services.LeaseService;

@Controller
@AllArgsConstructor
public class RentCollectionGraphQlController
{
  private final LeaseService leaseService;

  @QueryMapping
  public RentCollectionSummary leasePaymentStatus(@Argument Long leaseId)
  {
    return leaseService.getLeasePaymentStatus(leaseId);
  }
}
