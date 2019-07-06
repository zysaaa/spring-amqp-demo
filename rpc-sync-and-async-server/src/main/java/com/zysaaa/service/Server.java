package com.zysaaa.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zysaaa.Message;

import lombok.extern.slf4j.Slf4j;

import static com.zysaaa.Config.*;

@Service
@Slf4j
public class Server {

  private ObjectMapper objectMapper = new ObjectMapper();

  @RabbitListener(queues = SYNC_QUEUE_NAME)
  public Message listenAndResponseImmediately(@Payload Message message) {
    log.info("server receive msg: {},and will response to client immediately", message);
    return new Message(UUID.randomUUID().toString(), "hi this is server,i response to you immediately");
  }

  @RabbitListener(queues = SYNC_QUEUE_NAME_USING_MESSAGE)
  public org.springframework.amqp.core.Message listenUsingMessage(@Payload org.springframework.amqp.core.Message message) throws JsonProcessingException {
    log.info("server receive msg: {}", message);
    return MessageBuilder.withBody(objectMapper.writeValueAsBytes
      (new Message(UUID.randomUUID().toString(), "this is server in org.springframework.amqp.core.Message"))).build();
  }

  @RabbitListener(queues = ASYNC_QUEUE_NAME)
  public Message listen(@Payload Message message) throws InterruptedException {
    log.info("server receive msg: {},and will response to client for a while", message);
    // simulation
    TimeUnit.SECONDS.sleep(3);
    return new Message(UUID.randomUUID().toString(), "hi this is server,i response to you for a while," +
      "you should wait for my response asynchronously");
  }


  @RabbitListener(queues = ASYNC_USING_TEMPLATE_QUEUE_NAME)
  public org.springframework.amqp.core.Message listenUsingAsyncTemplate(@Payload org.springframework.amqp.core.Message message) throws InterruptedException, JsonProcessingException {
    log.info("server receive msg: {},and will response to client for a while", message);
    // simulation
    TimeUnit.SECONDS.sleep(3);
    return MessageBuilder.withBody(objectMapper.writeValueAsBytes
        (new Message(UUID.randomUUID().toString(), "hi this is server using template"))).setContentType(MediaType.APPLICATION_JSON_VALUE).build();
  }



}
