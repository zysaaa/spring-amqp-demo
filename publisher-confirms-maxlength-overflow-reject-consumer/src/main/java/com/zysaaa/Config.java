package com.zysaaa;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
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

  public static final String TOPIC_EXCHANGE_NAME_DURABLE = "topicexchange_name_2";

  public static final String DURABLE_QUEUE = "durable_queue_2";

  @Bean
  public AmqpTemplate amqpTemplate(@Autowired CachingConnectionFactory amqpConnectionFactory) {
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
    // 最多囤积10个，溢出则会采取 reject-publish策略
    return QueueBuilder.durable(DURABLE_QUEUE).withArgument("x-max-length", 10).withArgument("x-overflow", "reject-publish").build();
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
