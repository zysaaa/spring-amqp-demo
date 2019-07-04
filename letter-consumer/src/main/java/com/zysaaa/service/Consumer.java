package com.zysaaa.service;

import static com.zysaaa.Config.DURABLE_QUEUE;
import static com.zysaaa.Config.TOPIC_EXCHANGE_NAME;
import static org.springframework.amqp.core.ExchangeTypes.TOPIC;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.zysaaa.Message;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Consumer {

  /**
   * 这是一种临时匿名队列，服务起来自动生成，并且和指定的 exchange 绑定。下同。
   *
   * @param message 收到的消息
   */
  @RabbitListener(bindings = @QueueBinding(value = @org.springframework.amqp.rabbit.annotation.Queue, key = "*.add",
    exchange = @Exchange(type = TOPIC, value = TOPIC_EXCHANGE_NAME)))
  public void listenAdd(@Payload Message message) {
    log.info("receive user add msg: {}", message);
  }

  @RabbitListener(bindings = @QueueBinding(value = @org.springframework.amqp.rabbit.annotation.Queue, key = "*.delete",
    exchange = @Exchange(type = TOPIC, value = TOPIC_EXCHANGE_NAME)))
  public void listenDelete(@Payload Message message) {
    log.info("receive user delete msg: {}", message);
  }

  /**
   * 持久的队列，在{@link com.zysaaa.Config}中声明，且和对应 {@link com.zysaaa.Config#TOPIC_EXCHANGE_NAME_DURABLE} 绑定
   * 其 <code>Binding key</code>为 *.* 格式，所以能收到 add 和 delete 事件
   *
   * @see com.zysaaa.Config
   * @param message 收到的消息
   */
  @RabbitListener(queues = DURABLE_QUEUE)
  public void listenAddAndDelete(@Payload Message message) {
    log.info("receive user msg: {}", message);
  }
}
