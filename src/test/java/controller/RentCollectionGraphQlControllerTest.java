package controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tz.tante.rent.manager.controllers.graphql.RentCollectionGraphQlController;
import tz.tante.rent.manager.engines.rent.dtos.RentCollectionSummary;
import tz.tante.rent.manager.enums.RentCollectionStatus;
import tz.tante.rent.manager.services.LeaseService;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@GraphQlTest(RentCollectionGraphQlController.class)
class RentCollectionGraphQlControllerTest
{

  @Autowired
  GraphQlTester graphQlTester;

  @MockitoBean
  LeaseService leaseService;


  void testGetRentCollection() {
    when(leaseService.getLeasePaymentStatus(1L))
      .thenReturn(new RentCollectionSummary(
        1L,
        new BigDecimal("1000"),
        new BigDecimal("1000"),
        BigDecimal.ZERO,
        RentCollectionStatus.PAID,
        null
      ));

    graphQlTester.document("""
        query {
            leasePaymentStatus(leaseId: 1) {
                status
            }
        }
        """)
      .execute()
      .path("leasePaymentStatus.status")
      .entity(String.class)
      .isEqualTo("PAID");
  }
}