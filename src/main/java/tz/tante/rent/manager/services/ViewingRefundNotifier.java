package tz.tante.rent.manager.services;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tz.tante.rent.manager.clients.PaymentServiceClient;

@Service
@AllArgsConstructor
public class ViewingRefundNotifier
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ViewingRefundNotifier.class);

  private final PaymentServiceClient paymentServiceClient;

  public void requestRefund(String bookingReference, String reason)
  {
    try
    {
      paymentServiceClient.requestRefund(bookingReference, reason);
    }
    catch (Exception exception)
    {
      LOGGER.error("Could not request refund for booking {}: {}", bookingReference, exception.getMessage());
    }
  }
}
