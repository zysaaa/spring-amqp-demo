package com.zysaaa.service;

import static com.zysaaa.Config.ADD;
import static com.zysaaa.Config.DELETE;
import static com.zysaaa.Config.TOPIC_EXCHANGE_NAME;
import static com.zysaaa.Config.TOPIC_EXCHANGE_NAME_DURABLE;

import java.util.UUID;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zysaaa.Message;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Producer {

  @Autowired
  private AmqpTemplate amqpTemplate;

  @Scheduled(fixedDelay = 5000L)
  public void produce() {
    Message add = new Message(UUID.randomUUID().toString(), "user add");
    Message delete = new Message(UUID.randomUUID().toString(), "user delete");
    log.info("send add type msg: {} to server", add);
    log.info("send delete type msg: {} to server", delete);
    amqpTemplate.convertAndSend(TOPIC_EXCHANGE_NAME, ADD, add);
    amqpTemplate.convertAndSend(TOPIC_EXCHANGE_NAME, DELETE, delete);
    amqpTemplate.convertAndSend(TOPIC_EXCHANGE_NAME_DURABLE, ADD, add);
    amqpTemplate.convertAndSend(TOPIC_EXCHANGE_NAME_DURABLE, DELETE, delete);
  }
}
