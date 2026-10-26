package tz.tante.rent.manager.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tz.tante.rent.manager.messaging.models.NotificationDTO;
import static tz.tante.rent.manager.utilities.Constant.RABBITMQ_NOTIFICATION_QUEUE;

@Component
public class RabbitMQConsumer
{

  @RabbitListener(queues = RABBITMQ_NOTIFICATION_QUEUE)
  public void receive(NotificationDTO notification)
  {
    System.out.println("Consuming Message:" + notification.id());
  }
}