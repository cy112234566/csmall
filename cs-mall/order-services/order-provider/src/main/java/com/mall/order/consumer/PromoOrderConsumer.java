package com.mall.order.consumer;

import com.alibaba.fastjson.JSON;
import com.mall.order.OrderPromoService;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.dto.CreateSeckillOrderRequest;
import com.mall.order.dto.CreateSeckillOrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class PromoOrderConsumer {
    @Value("${mq.nameserver.addr}")
    private String addr;
    @Value("${mq.topicname2}")
    private String topicName;

    private DefaultMQPushConsumer mqConsumer;

    @Autowired
    private OrderPromoService orderPromoService;

    @PostConstruct
    public void init() throws MQClientException {
        log.info("PromoOrderConsumer -> 初始化...,topic:{},addre:{} ", topicName, addr);
        mqConsumer = new DefaultMQPushConsumer("promo_order_group");
        mqConsumer.setNamesrvAddr(addr);
        mqConsumer.subscribe(topicName,"*");

        // 消费一个创建秒杀订单的消息
        mqConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                MessageExt messageExt = msgs.get(0);
                byte[] body = messageExt.getBody();
                String bodyStr = new String(body);

                /**
                 * // 生成订单
                 *         CreateSeckillOrderRequest createSeckillOrderRequest = new CreateSeckillOrderRequest();
                 *         createSeckillOrderRequest.setUsername(request.getUsername());
                 *         createSeckillOrderRequest.setUserId(request.getUserId());
                 *         createSeckillOrderRequest.setProductId(request.getProductId());
                 *         createSeckillOrderRequest.setPrice(promoItem.getSeckillPrice());
                 *         CreateSeckillOrderResponse createSeckillOrderResponse = orderPromoService.createPromoOrder(createSeckillOrderRequest);
                 */
                Map map = JSON.parseObject(bodyStr, Map.class);
                String username = (String) map.get("username");
                Long userId = (Long) map.get("userId");
                Long productId = (Long) map.get("productId");
                BigDecimal price = (BigDecimal) map.get("price");

                CreateSeckillOrderRequest createSeckillOrderRequest = new CreateSeckillOrderRequest();
                createSeckillOrderRequest.setUsername(username);
                createSeckillOrderRequest.setUserId(userId);
                createSeckillOrderRequest.setProductId(productId);
                createSeckillOrderRequest.setPrice(price);
                CreateSeckillOrderResponse response = orderPromoService.createPromoOrder(createSeckillOrderRequest);

                if (response.getCode().equals(OrderRetCode.SUCCESS.getCode())){
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }else{
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
        });

        mqConsumer.start();
    }

}
