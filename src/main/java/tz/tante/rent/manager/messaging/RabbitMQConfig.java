package tz.tante.rent.manager.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

  public static final String EXCHANGE = "rent.payment.exchange";
  public static final String QUEUE = "rent.payment.queue";
  public static final String ROUTING_KEY = "rent.payment.received";
  public static final String DLX = "rent.payment.dlx";
  public static final String DLQ = "rent.payment.dlq";
  public static final String DLQ_ROUTING_KEY = "rent.payment.failed";

  @Bean
  DirectExchange rentPaymentDeadLetterExchange()
  {
    return new DirectExchange(DLX, true, false);
  }

  @Bean
  Queue rentPaymentDeadLetterQueue()
  {
    return new Queue(DLQ, true);
  }

  @Bean
  Binding rentPaymentDeadLetterBinding(Queue rentPaymentDeadLetterQueue, DirectExchange rentPaymentDeadLetterExchange)
  {
    return BindingBuilder
      .bind(rentPaymentDeadLetterQueue)
      .to(rentPaymentDeadLetterExchange)
      .with(DLQ_ROUTING_KEY);
  }

  @Bean
  DirectExchange rentPaymentExchange()
  {
    return new DirectExchange(EXCHANGE, true, false);
  }

  @Bean
  Queue rentPaymentQueue() {
    return new Queue(
      QUEUE,
      true,
      false,
      false,
      java.util.Map.of(
        "x-dead-letter-exchange", DLX,
        "x-dead-letter-routing-key", DLQ_ROUTING_KEY
      )
    );
  }

  @Bean
  Binding rentPaymentBinding(Queue rentPaymentQueue, DirectExchange rentPaymentExchange) {
    return BindingBuilder
      .bind(rentPaymentQueue)
      .to(rentPaymentExchange)
      .with(ROUTING_KEY);
  }

  @Bean
  MessageConverter jacksonMessageConverter()
  {
    return new Jackson2JsonMessageConverter();
  }
}