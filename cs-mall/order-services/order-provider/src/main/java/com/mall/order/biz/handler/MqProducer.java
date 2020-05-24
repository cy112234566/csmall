package com.mall.order.biz.handler;

import com.alibaba.fastjson.JSON;
import com.mall.order.dto.CancelOrderRequest;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
@Component
public class MqProducer {

    private DefaultMQProducer defaultMQProducer;

    @PostConstruct
    public void init() throws MQClientException {
        defaultMQProducer = new DefaultMQProducer("order_producer_group");

        defaultMQProducer.setNamesrvAddr("localhost:9876");

        defaultMQProducer.start();
    }

    /**
     * 发送订单延迟消息
     * @param request
     */
    public void sendOrderMessage(CancelOrderRequest request) throws UnsupportedEncodingException {

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("cancelOrderRequest",request);

        Message message = new Message("order_topic", JSON.toJSONString(hashMap).getBytes("utf-8"));

        // 30m
        message.setDelayTimeLevel(16);

        try {
            defaultMQProducer.send(message);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
