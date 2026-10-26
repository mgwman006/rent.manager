package tz.tante.rent.manager.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import tz.tante.rent.manager.messaging.models.NotificationDTO;
import static tz.tante.rent.manager.utilities.Constant.RABBITMQ_NOTIFICATION_EXCHANGE;
import static tz.tante.rent.manager.utilities.Constant.RABBITMQ_NOTIFICATION_RECEIVED_ROUTING_KEY;

@Component
public class RabbitMQPublisher
{
  private final RabbitTemplate rabbitTemplate;

  public RabbitMQPublisher(RabbitTemplate rabbitTemplate)
  {
    this.rabbitTemplate = rabbitTemplate;
  }

  public void publish(NotificationDTO message)
  {
    rabbitTemplate.convertAndSend(RABBITMQ_NOTIFICATION_EXCHANGE, RABBITMQ_NOTIFICATION_RECEIVED_ROUTING_KEY, message);
  }
}
