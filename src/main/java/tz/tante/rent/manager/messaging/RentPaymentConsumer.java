package tz.tante.rent.manager.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tz.tante.rent.manager.messaging.models.RentPaymentReceived;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RentPaymentConsumer {

  private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

  @RabbitListener(queues = RabbitMQConfig.QUEUE)
  public void receive(RentPaymentReceived event) {

//    if (!processedEventIds.add(event.eventId())) {
//      System.out.println(
//        "Duplicate ignored: " + event.eventId()
//      );
//      return;
//    }

    System.out.println("Processing payment event:");
    throw new RuntimeException("Simulated payment processing failure");
//    System.out.println("Event ID: " + event.eventId());
//    System.out.println("Lease ID: " + event.leaseId());
//    System.out.println("Amount: " + event.amount());
  }
}