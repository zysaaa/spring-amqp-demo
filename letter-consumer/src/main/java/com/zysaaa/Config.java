package com.zysaaa;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ComponentScan("com.zysaaa.service")
public class Config {

  public static final String TOPIC_EXCHANGE_NAME = "topicexchange_name";
  public static final String TOPIC_EXCHANGE_NAME_DURABLE = "topicexchange_name_d";

  public static final String DURABLE_QUEUE = "durable_queue";

  @Bean
  public AmqpTemplate amqpTemplate(@Autowired ConnectionFactory amqpConnectionFactory) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(amqpConnectionFactory);
    rabbitTemplate.setMessageConverter(jsonMessageConverter());
    return rabbitTemplate;
  }

  @Bean
  public MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public Queue queue() {
    return new Queue(DURABLE_QUEUE);  //durable, non-exclusive and non auto-delete.
  }

  @Bean
  public Binding binding() {
    return BindingBuilder.bind(queue()).to(topicExchangeDurable()).with("*.*");  //转发所有routingKey格式为 *.* 的消息
  }

  @Bean
  public TopicExchange topicExchangeDurable() {
    return new TopicExchange(TOPIC_EXCHANGE_NAME_DURABLE);
  }

}
