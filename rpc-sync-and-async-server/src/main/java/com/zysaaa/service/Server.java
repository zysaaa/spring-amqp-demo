package com.zysaaa.service;

import static com.zysaaa.Config.ASYNC_QUEUE_NAME;
import static com.zysaaa.Config.ASYNC_RECEIVE_QUEUE_NAME;
import static com.zysaaa.Config.SYNC_QUEUE_NAME;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.zysaaa.Message;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Server {

  @RabbitListener(queues = SYNC_QUEUE_NAME)
  public Message listenAndResponseImmediately(@Payload Message message) {
    log.info("server receive msg: {},and will response to client immediately", message);
    return new Message(UUID.randomUUID().toString(), "hi this is server,i response to you immediately");
  }

  @RabbitListener(queues = ASYNC_QUEUE_NAME)
  public Message listen(@Payload Message message) throws InterruptedException {
    log.info("server receive msg: {},and will response to client for a while", message);
    // simulation
    TimeUnit.SECONDS.sleep(3);
    return new Message(UUID.randomUUID().toString(), "hi this is server,i response to you for a while," +
      "you should wait for my response asynchronously");
  }

}
