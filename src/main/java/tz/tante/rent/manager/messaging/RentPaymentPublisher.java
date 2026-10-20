package tz.tante.rent.manager.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import tz.tante.rent.manager.messaging.models.RentPaymentReceived;

@Component
public class RentPaymentPublisher {

  private final RabbitTemplate rabbitTemplate;

  public RentPaymentPublisher(RabbitTemplate rabbitTemplate)
  {
    this.rabbitTemplate = rabbitTemplate;
  }

  public void publish(RentPaymentReceived event)
  {
    rabbitTemplate.convertAndSend("rent.payment.exchange", "rent.payment.received", event);
  }
}
