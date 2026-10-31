package tz.tante.rent.manager.messaging;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tz.tante.rent.manager.messaging.models.NotificationDTO;
import tz.tante.rent.manager.messaging.notification.SmsProvider;
import tz.tante.rent.manager.messaging.notification.provider.afrikatalking.AfrikaTalkingSmsProvider;

import static tz.tante.rent.manager.utilities.Constant.RABBITMQ_NOTIFICATION_QUEUE;

@Component
@AllArgsConstructor
public class RabbitMQConsumer
{
  private final AfrikaTalkingSmsProvider smsProvider;

//  @RabbitListener(queues = RABBITMQ_NOTIFICATION_QUEUE)
  public void receive(NotificationDTO notification)
  {
    smsProvider.sendSms(notification.recipientPhoneNumber(), notification.message());
  }
}