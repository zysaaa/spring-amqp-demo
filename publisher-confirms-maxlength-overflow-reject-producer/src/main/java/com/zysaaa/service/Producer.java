package com.zysaaa.service;

import com.zysaaa.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.zysaaa.Config.ADD;
import static com.zysaaa.Config.TOPIC_EXCHANGE_NAME_DURABLE;

@Service
@Slf4j
public class Producer {

  @Autowired
  private RabbitTemplate amqpTemplate;

  @Scheduled(fixedDelay = 1000L)
  public void produce() {
    Message add = new Message(UUID.randomUUID().toString(), "user add");
    amqpTemplate.convertAndSend(TOPIC_EXCHANGE_NAME_DURABLE, ADD, add);
  }
}
