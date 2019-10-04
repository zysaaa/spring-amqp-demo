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

  public static final String NORMAL_QUEUE_NAME = "normal_queue_name_for_delay";
  public static final String DELAY_QUEUE_NAME = "delay_queue_name";

  public static final String DELAY_EXCHANGE_NAME = "delay_exchange_name";

  /** 注意，分别为 .normal形式和 .dead形式，匹配 exchange 和 queue 的 bindingkey。 */
  public static final String DELAY_ROUTING_KEY = "msg.delay";

  /**
   * dead-msg 会带着 {@link #DELAY_ROUTING_KEY} 的 RoutingKey 由 {@link #DELAY_EXCHANGE_NAME} 进行转发
   *
   * @see <a href="https://www.rabbitmq.com/dlx.html">Dead Letter Exchanges</a>
   * @see #DELAY_ROUTING_KEY
   * @see #DELAY_EXCHANGE_NAME
   * @return 队列。
   */
  @Bean
  public Queue queue() {
    return QueueBuilder.durable(NORMAL_QUEUE_NAME)
      .withArgument("x-dead-letter-exchange", DELAY_EXCHANGE_NAME)
      .withArgument("x-dead-letter-routing-key", DELAY_ROUTING_KEY)
      .withArgument("x-message-ttl", 5000)
      .build();
  }

  @Bean
  public Queue deadMsgQueue() {
    return new Queue(DELAY_QUEUE_NAME); // durable, non-exclusive and non auto-delete.
  }

  @Bean
  public MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

}
