package com.zysaaa.service;

import static com.zysaaa.Config.DEAD_QUEUE_NAME;
import static com.zysaaa.Config.NORMAL_QUEUE_NAME;

import java.io.IOException;
import java.util.Random;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;
import com.zysaaa.Message;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Consumer {

  private Random random = new Random();

  /**
   * 用于接收 normal 的信息，并且有概率性的拒绝消息让消息回到某个 exchange 进行重新分发。
   * 具体规则查看 {@link com.zysaaa.Config} 的配置
   *
   * <p>
   *     想让一个消息进行重新分发，满足以下条件即可：
   *     <ul>
   *        <li>The message is negatively acknowledged by a consumer using basic.reject or basic.nack with requeue parameter set to false.</li>
   *        <li>The message expires due to per-message TTL.</li>
   *        <li>The message is dropped because its queue exceeded a length limit.</li>
   *     </ul>
   *     本例采用的是第一种。
   * </p>
   *
   * <p>
   *     注意，请在配置文件里配置 auto-ack 为手动（listener.acknowledge-mode: manual）模式。
   *     因为默认是自动 ack 的。
   *     其实也可以抛出一个 {@link org.springframework.amqp.AmqpRejectAndDontRequeueException} 来达到让消息回到
   *     exchange 重新分发的效果，且不用配置 auto-ack 为手动模式。
   * </p>
   *
   * @see <a href="https://www.rabbitmq.com/dlx.html">Dead Letter Exchanges</a>
   * @see org.springframework.amqp.AmqpRejectAndDontRequeueException
   * @see com.zysaaa.Config
   * @param message 传递的消息
   * @param channel 通道
   * @param tag 传递 tag
   * @throws IOException 异常
   *
   */
  @RabbitListener(queues = NORMAL_QUEUE_NAME)
  public void listenNormalMsg
     (@Payload Message message,
     Channel channel,
     @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
    log.info("receive normal msg: {}", message);
    if (random.nextBoolean()) {
      log.info("i will reject this message:{}, " +
        "and this message will go back to some appointed exchange and be distributed again.", message);
      channel.basicReject(tag, false);  // requeue must be false
    } else {
      channel.basicAck(tag, false);
    }
  }


  /**
   * 重新分发的消息会送回到{@link com.zysaaa.Config#DEAD_EXCHANGE_NAME}，
   * 最终分发到 {@link com.zysaaa.Config#DEAD_QUEUE_NAME} 这个队列。
   *
   * @see com.zysaaa.Config#DEAD_QUEUE_NAME
   * @see com.zysaaa.Config#DEAD_EXCHANGE_NAME
   * @param message 传递的消息
   * @param channel 通道
   * @param tag 传递 tag
   * @throws IOException 异常
   */
  @RabbitListener(queues = DEAD_QUEUE_NAME)
  public void listenDeadMsg
    (@Payload Message message,
      Channel channel,
      @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
    log.info("receive dead msg: {}", message);
    channel.basicAck(tag, false);
  }
}
