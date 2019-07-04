package com.zysaaa;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.zysaaa.service")
public class Config {

  public static final String NORMAL_QUEUE_NAME = "normal_queue_name";
  public static final String DEAD_QUEUE_NAME = "dead_queue_name";

  public static final String DEAD_EXCHANGE_NAME = "dead_exchange_name";

  public static final String DEAD_ROUTING_KEY = "msg.dead";

  /**
   * dead-msg 会带着 {@link #DEAD_ROUTING_KEY} 的 RoutingKey 由 {@link #DEAD_EXCHANGE_NAME} 进行转发
   *
   * @see <a href="https://www.rabbitmq.com/dlx.html">Dead Letter Exchanges</a>
   * @see #DEAD_ROUTING_KEY
   * @see #DEAD_EXCHANGE_NAME
   * @return 队列。
   */
  @Bean
  public Queue queue() {
    return QueueBuilder.durable(NORMAL_QUEUE_NAME)
      .withArgument("x-dead-letter-exchange", DEAD_EXCHANGE_NAME)
      .withArgument("x-dead-letter-routing-key", DEAD_ROUTING_KEY)
      .build();
  }

  @Bean
  public Queue deadMsgQueue() {
    return new Queue(DEAD_QUEUE_NAME); // durable, non-exclusive and non auto-delete.
  }

  @Bean
  public MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

}
