package com.zysaaa.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Address;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zysaaa.Message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;

import static com.zysaaa.Config.*;

@Service
@Slf4j
public class Client {

  @Autowired
  private AmqpTemplate amqpTemplate;

  @Autowired
  private AsyncRabbitTemplate asyncRabbitTemplate;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Scheduled(fixedDelay = 5000L)
  public void sendAndReceiveSync() {
    Message message = new Message(UUID.randomUUID().toString(), "hi this is client, i need ur reply right now,i'm waiting");
    log.info("send msg: {} to server and wait for response sync", message);
    Object response = amqpTemplate.convertSendAndReceive(SYNC_EXCHANGE_NAME, SYNC_ROUTING_KEY, message);
    log.info("receive response sync: {}", response);
  }

  @Scheduled(fixedDelay = 5000L)
  public void sendAndReceiveSyncUsingMessage() throws IOException {
    Message message = new Message(UUID.randomUUID().toString(), "hi this is client in org.springframework.amqp.core.Message");
    org.springframework.amqp.core.Message messageToSend = MessageBuilder
      .withBody(objectMapper.writeValueAsBytes(message))
        .setContentType(MediaType.APPLICATION_JSON_VALUE).build();
    org.springframework.amqp.core.Message response = amqpTemplate.sendAndReceive(SYNC_EXCHANGE_NAME, SYNC_ROUTING_KEY_USING_MESSAGE, messageToSend);
    byte[] responseBody = response.getBody();
    Message responseMessage = objectMapper.readValue(responseBody, Message.class);
    log.info("receive response using org.springframework.amqp.core.Message: {}", responseMessage);
  }

  /**
   * 发送的消息，配置 ReplyTo 属性，告诉对方，在你收到消息之后，你需要转发携带着指定的 routingKey 转发到某个 exchange。
   *
   */
  @Scheduled(fixedDelay = 5000L)
  public void sendAndReceiveAsync() {
    Message message = new Message(UUID.randomUUID().toString(), "hi this is client, u can reply me a little while");
    log.info("send msg: {} to server and wait for response async", message);
    amqpTemplate.convertAndSend(ASYNC_EXCHANGE_NAME, ASYNC_ROUTING_KEY, message, new MessagePostProcessor() {
      @Override
      public org.springframework.amqp.core.Message postProcessMessage(org.springframework.amqp.core.Message message) throws AmqpException {
        // 转发规则
        message.getMessageProperties().setReplyToAddress(new Address(ASYNC_EXCHANGE_NAME, ASYNC_RECEIVE_ROUTING_KEY));
        return message;
      }
    });
  }

  /**
   * 异步接收回应，此处指定的 ASYNC_RECEIVE_QUEUE_NAME 会在配置中和对应 exchange 绑定。
   *
   * @param message 对方回应的消息，异步接收。
   */
  @RabbitListener(queues = ASYNC_RECEIVE_QUEUE_NAME)
  public void receiveAsyncResponse(@Payload Message message) {
    log.info("receive response async: {}", message);
  }

  @Scheduled(fixedDelay = 5000L)
  public void sendUsingAsyncTemplate() throws JsonProcessingException {
    Message message = new Message(UUID.randomUUID().toString(), "hi this is client using async template");
    log.info("send msg: {} to server using async template ", message);
    org.springframework.amqp.core.Message messageToSend = MessageBuilder
      .withBody(objectMapper.writeValueAsBytes(message))
        .setContentType(MediaType.APPLICATION_JSON_VALUE).build();
    AsyncRabbitTemplate.RabbitMessageFuture future = asyncRabbitTemplate.sendAndReceive(ASYNC_EXCHANGE_NAME, ASYNC_TEMPLATE_ROUTING_KEY ,messageToSend);
    future.addCallback(new SuccessCallback<org.springframework.amqp.core.Message>() {
      @Override
      public void onSuccess(org.springframework.amqp.core.Message message) {
        log.info("receive response using async template: {}", message);
      }
    }, new FailureCallback() {
      @Override
      public void onFailure(Throwable throwable) {
        log.error("fail to receive response using async template", throwable);
      }
    });
  }


}
