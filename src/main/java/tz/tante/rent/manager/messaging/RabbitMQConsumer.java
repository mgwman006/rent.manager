package tz.tante.rent.manager.messaging;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import tz.tante.rent.manager.messaging.models.NotificationDTO;
import tz.tante.rent.manager.messaging.notification.SmsProvider;


@Component
@AllArgsConstructor
public class RabbitMQConsumer
{
  private final SmsProvider smsProvider;

//  @RabbitListener(queues = RABBITMQ_NOTIFICATION_QUEUE)
  public void receive(NotificationDTO notification)
  {
    smsProvider.sendSms(notification.recipientPhoneNumber(), notification.message());
  }
}