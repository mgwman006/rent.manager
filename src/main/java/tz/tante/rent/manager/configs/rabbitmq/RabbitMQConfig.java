package tz.tante.rent.manager.configs.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

import java.util.Map;

import static tz.tante.rent.manager.utilities.Constant.*;

public class RabbitMQConfig
{
  @Bean
  DirectExchange notificationDeadLetterExchange()
  {
    return new DirectExchange(RABBITMQ_NOTIFICATION_DEAD_LETTER_EXCHANGE, true, false);
  }

  @Bean
  Queue notificationDeadLetterQueue()
  {
    return new Queue(RABBITMQ_NOTIFICATION_DEAD_LETTER_QUEUE, true);
  }

  @Bean
  Binding notificationDeadLetterBinding(Queue notificationDeadLetterQueue, DirectExchange notificationDeadLetterExchange)
  {
    return BindingBuilder
      .bind(notificationDeadLetterQueue)
      .to(notificationDeadLetterExchange)
      .with(RABBITMQ_NOTIFICATION_DEAD_LETTER_ROUTING_KEY);
  }



  @Bean
  DirectExchange notificationExchange()
  {
    return new DirectExchange(RABBITMQ_NOTIFICATION_EXCHANGE, true, false);
  }

  @Bean
  Queue notificationQueue() {
    return new Queue(
      RABBITMQ_NOTIFICATION_QUEUE,
      true,
      false,
      false,
      Map.of(
        "x-dead-letter-exchange", RABBITMQ_NOTIFICATION_DEAD_LETTER_EXCHANGE,
        "x-dead-letter-routing-key", RABBITMQ_NOTIFICATION_DEAD_LETTER_ROUTING_KEY
      )
    );
  }

  @Bean
  Binding notificationBinding(Queue notificationQueue, DirectExchange notificationExchange) {
    return BindingBuilder
      .bind(notificationQueue)
      .to(notificationExchange)
      .with(RABBITMQ_NOTIFICATION_RECEIVED_ROUTING_KEY);
  }

  @Bean
  MessageConverter jacksonMessageConverter()
  {
    return new Jackson2JsonMessageConverter();
  }
}