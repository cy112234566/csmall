package com.mall.promo.mq;

import com.alibaba.fastjson.JSON;
import com.mall.promo.cache.CacheManager;
import com.mall.promo.dal.persistence.PromoItemMapper;
import com.mall.promo.dto.CreatePromoOrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cy
 * @date 2020/5/26 22:18
 */
@Component
@Slf4j
public class MqTransactionProducer {
   private TransactionMQProducer transactionMQProducer;

   @Value("${mq.nameserver.addr}")
   String addr;

   @Value("${mq.topicname}")
   String topic;

   @Autowired
   private PromoItemMapper promoItemMapper;

   @Autowired
   private CacheManager cacheManager;

   @PostConstruct
    public void init(){
       transactionMQProducer = new TransactionMQProducer("promo_group");
       transactionMQProducer.setNamesrvAddr(addr);
       try {
           transactionMQProducer.start();
       } catch (MQClientException e) {
           e.printStackTrace();
       }

       //创建一个事务监听器
       transactionMQProducer.setTransactionListener(new TransactionListener() {
           //执行本地事务 = 扣减库存
           @Override
           public LocalTransactionState executeLocalTransaction(Message message, Object o) {
               HashMap<String,Object> argsMap = (HashMap<String, Object>) o;
               Long productId = (Long) argsMap.get("productId");
               Long psId = (Long) argsMap.get("psId");
               Integer rows = promoItemMapper.decreaseStock(productId,psId);
               String key = "promo_order_id_"+message.getTransactionId();
               if (rows < 1){
                   String value = "fail";
                   cacheManager.setCache(key,value,1);
                   return LocalTransactionState.ROLLBACK_MESSAGE;
               }
               String value = "success";
               cacheManager.setCache(key,value,1);
               return LocalTransactionState.COMMIT_MESSAGE;
           }

           //检查本地事务
           //怎么检查本地事务的执行结果呢  这里可以考虑用redis解决
           @Override
           public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
               String key = "promo_order_id_"+messageExt.getTransactionId();
               String value = cacheManager.checkCache(key);
               if ("fail".equals(value)){
                   return LocalTransactionState.ROLLBACK_MESSAGE;
               }
               if ("success".equals(value)){
                   return LocalTransactionState.COMMIT_MESSAGE;
               }
               return LocalTransactionState.UNKNOW;
           }
       });
   }


    public Boolean sendPromoOrderTransaction(CreatePromoOrderRequest createPromoOrderRequest) {
        //构建参数
        HashMap<String, Object> map = new HashMap<>();
        map.put("username",createPromoOrderRequest.getUsername());
        map.put("userId",createPromoOrderRequest.getUserId());
        map.put("productId",createPromoOrderRequest.getProductId());
        map.put("price",createPromoOrderRequest.getPromoPrice());
        Message message = new Message(topic, JSON.toJSONString(map).getBytes(Charset.forName("utf-8")));


        HashMap<String,Object> argsMap = new HashMap<>();
        argsMap.put("productId",createPromoOrderRequest.getProductId());
        argsMap.put("psId",createPromoOrderRequest.getPsId());
        TransactionSendResult transactionSendResult = new TransactionSendResult();
        try {
            //发送事务型消息
            transactionSendResult = transactionMQProducer.sendMessageInTransaction(message,argsMap);
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        if (transactionSendResult == null || transactionSendResult.getLocalTransactionState() == null) {
            return false;
        }
        //查看消息发送的结果
        LocalTransactionState localTransactionState = transactionSendResult.getLocalTransactionState();
        if (localTransactionState.equals(LocalTransactionState.COMMIT_MESSAGE)){
            return true;
        }else if (localTransactionState.equals(LocalTransactionState.ROLLBACK_MESSAGE)){
            return false;
        }else {
            return false;
        }


    }
}
