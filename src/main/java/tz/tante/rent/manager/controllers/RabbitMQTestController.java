package tz.tante.rent.manager.controllers;

import org.springframework.web.bind.annotation.*;
import tz.tante.rent.manager.messaging.RentPaymentPublisher;
import tz.tante.rent.manager.messaging.models.RentPaymentReceived;

import java.util.UUID;

@RestController
@RequestMapping("/api/test/rabbitmq")
public class RabbitMQTestController {

  private final RentPaymentPublisher publisher;

  public RabbitMQTestController(RentPaymentPublisher publisher)
  {
    this.publisher = publisher;
  }

  @PostMapping("/publish")
  public String publish()
  {
    var event = new RentPaymentReceived(UUID.randomUUID().toString(), 101L, "150000.00");
    publisher.publish(event);
    return "Published event: " + event.eventId();
  }
  @PostMapping("/publish/with-event-id")
  public String publish(@RequestParam String eventId) {
    var event = new RentPaymentReceived(
      eventId,
      101L,
      "150000.00"
    );

    publisher.publish(event);

    return "Published event: " + event.eventId();
  }
}