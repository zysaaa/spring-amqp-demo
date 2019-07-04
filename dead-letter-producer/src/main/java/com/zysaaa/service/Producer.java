package com.zysaaa.service;

import static com.zysaaa.Config.NORMAL_EXCHANGE_NAME;
import static com.zysaaa.Config.NORMAL_ROUTING_KEY;

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
    Message message = new Message(UUID.randomUUID().toString());
    log.info("produce and send message to exchange: {}", message);
    amqpTemplate.convertAndSend(NORMAL_EXCHANGE_NAME, NORMAL_ROUTING_KEY, message);
  }
}
