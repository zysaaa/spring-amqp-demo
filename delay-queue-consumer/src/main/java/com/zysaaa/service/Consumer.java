package com.zysaaa.service;

import com.rabbitmq.client.Channel;
import com.zysaaa.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

import static com.zysaaa.Config.DELAY_QUEUE_NAME;

@Service
@Slf4j
public class Consumer {


  /**
   * 重新分发的消息会送回到{@link com.zysaaa.Config#DELAY_EXCHANGE_NAME}，
   * 最终分发到 {@link com.zysaaa.Config#DELAY_QUEUE_NAME} 这个队列。
   *
   * @see com.zysaaa.Config#DELAY_QUEUE_NAME
   * @see com.zysaaa.Config#DELAY_EXCHANGE_NAME
   * @param message 传递的消息
   * @param channel 通道
   * @param tag 传递 tag
   * @throws IOException 异常
   */
  @RabbitListener(queues = DELAY_QUEUE_NAME)
  public void listenDeadMsg
    (@Payload Message message,
      Channel channel,
      @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
    log.info("At {}, receive dead msg: {}", new Date(), message);
    channel.basicAck(tag, false);
  }
}
