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

import static com.zysaaa.Config.DURABLE_QUEUE;

@Service
@Slf4j
public class Consumer {

  /**
   * 持久的队列，在{@link com.zysaaa.Config}中声明，且和对应 {@link com.zysaaa.Config#TOPIC_EXCHANGE_NAME_DURABLE} 绑定
   * 其 <code>Binding key</code>为 *.* 格式，所以能收到 add 和 delete 事件
   *
   * @see com.zysaaa.Config
   * @param message 收到的消息
   */
  @RabbitListener(queues = DURABLE_QUEUE)
  public void listenAddAndDelete2(@Payload Message message,
                                  Channel channel,
                                  @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
    log.info("listener receive user msg: {}", message);
    // 不进行ack，让队列里的消息达到 max-length，以触发overflow策略，来确保publisher-confirms获得nack的结果。
    // 注意，这里请确保配置文件里配置了确认方式为手动确认
    // 由于qos默认为1，所以这个方法只会进来一次，直到这个消费者确认或者拒绝了消息。
    // 设置qos可以在配置文件里设置对应的参数 prefetch=xx
    // channel.basicReject(tag, false);
  }
}
