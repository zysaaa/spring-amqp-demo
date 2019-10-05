package com.zysaaa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
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
@Slf4j
public class Config {

  public static final String TOPIC_EXCHANGE_NAME_DURABLE = "topicexchange_name_2";

  public static final String ADD = "user.add";

  @Bean
  public RabbitTemplate amqpTemplate(@Autowired CachingConnectionFactory amqpConnectionFactory) {
    amqpConnectionFactory.setPublisherConfirms(true);
    RabbitTemplate rabbitTemplate = new RabbitTemplate(amqpConnectionFactory);
    rabbitTemplate.setMessageConverter(jsonMessageConverter());
    rabbitTemplate.setConfirmCallback(confirmCallback);
    return rabbitTemplate;
  }

  static RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
      if (ack) {
        log.info("publish success!");
      } else {
        log.info("publish failed!");
      }
    }
  };

  @Bean
  public MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }


}
