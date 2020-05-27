package com.mall.promo.services;

import com.mall.order.OrderPromoService;
import com.mall.order.dto.CreateSeckillOrderRequest;
import com.mall.order.dto.CreateSeckillOrderResponse;
import com.mall.promo.PromoService;
import com.mall.promo.cache.CacheManager;
import com.mall.promo.constants.PromoRetCode;
import com.mall.promo.converter.PromoItemConverter;
import com.mall.promo.dal.entitys.PromoItem;
import com.mall.promo.dal.entitys.PromoSession;
import com.mall.promo.dal.persistence.PromoItemMapper;
import com.mall.promo.dal.persistence.PromoSessionMapper;
import com.mall.promo.dto.*;
import com.mall.promo.mq.MqTransactionProducer;
import com.mall.shopping.IProductService;
import com.mall.shopping.dto.ProductDetailDto;
import com.mall.shopping.dto.ProductDetailRequest;
import com.mall.shopping.dto.ProductDetailResponse;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cy
 * @date 2020/5/25 15:05
 */
@Service
@Component
public class PromoServiceImpl implements PromoService {
    @Autowired
    private PromoSessionMapper promoSessionMapper;

    @Autowired
    private PromoItemMapper promoItemMapper;

    @Reference(check = false)
    IProductService iProductService;

    @Reference(check = false)
    OrderPromoService orderPromoService;

    @Autowired
    private PromoItemConverter promoItemConverter;

    @Autowired
    private MqTransactionProducer mqTransactionProducer;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public PromoInfoResponse getPromoList(PromoInfoRequest promoInfoRequest) {
        promoInfoRequest.requestCheck();
        //查询场次信息
        Example example = new Example(PromoSession.class);
        example.createCriteria().andEqualTo("sessionId",promoInfoRequest.getSessionId())
                .andEqualTo("yyyymmdd",promoInfoRequest.getYyyyMMdd());
        List<PromoSession> promoSessions = promoSessionMapper.selectByExample(example);
        PromoInfoResponse promoInfoResponse = new PromoInfoResponse();
        if (CollectionUtils.isEmpty(promoSessions)){
            promoInfoResponse.setCode(PromoRetCode.PROMO_NOT_EXIST.getCode());
            promoInfoResponse.setMsg(PromoRetCode.PROMO_NOT_EXIST.getMessage());
            return promoInfoResponse;
        }
        PromoSession promoSession = promoSessions.get(0);
        Integer sessionId = promoSession.getId();
        //查询场次商品关联表
        Example itemExample = new Example(PromoItem.class);
        itemExample.createCriteria().andEqualTo("psId",sessionId);
        List<PromoItem> promoItems = promoItemMapper.selectByExample(itemExample);
        if (CollectionUtils.isEmpty(promoItems)){
            promoInfoResponse.setCode(PromoRetCode.PROMO_ITEM_NOT_EXIST.getCode());
            promoInfoResponse.setMsg(PromoRetCode.PROMO_ITEM_NOT_EXIST.getMessage());
            return promoInfoResponse;
        }
        List<PromoItemInfoDto> productList = new ArrayList<>();
        //查询商品信息
        promoItems.stream().forEach(promoItem -> {
            Integer itemId = promoItem.getItemId();
            Long id = Long.valueOf(itemId);
            ProductDetailRequest productDetailRequest = new ProductDetailRequest();
            productDetailRequest.setId(id);
            ProductDetailResponse productDetailResponse = iProductService.getProductDetail(productDetailRequest);
            ProductDetailDto productDetailDto = productDetailResponse.getProductDetailDto();
            PromoItemInfoDto promoItemInfoDto = promoItemConverter.productDetailDto2promoItemDto(productDetailDto);
            promoItemInfoDto.setInventory(promoItem.getItemStock());
            promoItemInfoDto.setSeckillPrice(promoItem.getSeckillPrice());
            productList.add(promoItemInfoDto);
        });

        //组装参数
        promoInfoResponse.setPsId(sessionId);
        promoInfoResponse.setSessionId(promoInfoRequest.getSessionId());
        promoInfoResponse.setCode(PromoRetCode.SUCCESS.getCode());
        promoInfoResponse.setMsg(PromoRetCode.SUCCESS.getMessage());
        return promoInfoResponse;
    }

    @Override
    public CreatePromoOrderResponse createPromoOrder(CreatePromoOrderRequest createPromoOrderRequest) {
        CreatePromoOrderResponse response = new CreatePromoOrderResponse();
        //扣减库存
        Integer rows = promoItemMapper.decreaseStock(createPromoOrderRequest.getProductId(),createPromoOrderRequest.getPsId());
        if (rows < 1){
            response.setCode(PromoRetCode.PROMO_ITEM_STOCK_NOT_ENOUGH.getCode());
            response.setMsg(PromoRetCode.PROMO_ITEM_STOCK_NOT_ENOUGH.getMessage());
            return response;
        }
        //生成订单之前要先回去商品的秒杀价格
        Example example = new Example(PromoItem.class);
        example.createCriteria()
                .andEqualTo("psId", createPromoOrderRequest.getPsId())
                .andEqualTo("itemId", createPromoOrderRequest.getProductId());
        List<PromoItem> promoItems = promoItemMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(promoItems)) {
            response.setCode(PromoRetCode.PROMO_ITEM_NOT_EXIST.getCode());
            response.setCode(PromoRetCode.PROMO_ITEM_NOT_EXIST.getMessage());
            return response;
        }
        PromoItem promoItem = promoItems.get(0);
        //生成秒杀订单
        CreateSeckillOrderRequest createSeckillOrderRequest = new CreateSeckillOrderRequest();
        createSeckillOrderRequest.setUserId(createPromoOrderRequest.getUserId());
        createSeckillOrderRequest.setUsername(createPromoOrderRequest.getUsername());
        createSeckillOrderRequest.setProductId(createPromoOrderRequest.getProductId());
        createSeckillOrderRequest.setPrice(promoItem.getSeckillPrice());
        CreateSeckillOrderResponse promoOrder = orderPromoService.createPromoOrder(createSeckillOrderRequest);
        if (!promoOrder.getCode().equals(PromoRetCode.SUCCESS.getCode())) {
            response.setCode(promoOrder.getCode());
            response.setMsg(promoOrder.getMsg());
            return response;
        }
        response.setCode(PromoRetCode.SUCCESS.getCode());
        response.setMsg(PromoRetCode.SUCCESS.getMessage());
        response.setProductId(createPromoOrderRequest.getProductId());
        response.setInventory(promoItem.getItemStock());
        return response;
    }

    @Override
    public CreatePromoOrderResponse createPromoOrderInTransaction(CreatePromoOrderRequest createPromoOrderRequest) {
        createPromoOrderRequest.requestCheck();
        CreatePromoOrderResponse response = new CreatePromoOrderResponse();
        Example example = new Example(PromoItem.class);
        example.createCriteria()
                .andEqualTo("psId", createPromoOrderRequest.getPsId())
                .andEqualTo("itemId", createPromoOrderRequest.getProductId());
        List<PromoItem> promoItems1 = promoItemMapper.selectByExample(example);
        PromoItem promoItem1 = promoItems1.get(0);
        createPromoOrderRequest.setPromoPrice(promoItem1.getSeckillPrice());
        /**
         *当查到秒杀商品库存已经售罄的时候，不应该执行后面的代码了
         *限流的售罄问题优化
         * 可以考虑在redis里做标记
         *在下单之前进行查询
         */
        Integer stock = promoItem1.getItemStock();
        String key = "promo_item_stock_not_enough_"+createPromoOrderRequest.getPsId()+"_"+createPromoOrderRequest.getProductId();
        if (stock < 1){
            cacheManager.setCache(key,"stock_not_enough",1);
        }

        //发送事务型消息
        Boolean ret = mqTransactionProducer.sendPromoOrderTransaction(createPromoOrderRequest);
        List<PromoItem> promoItems2 = promoItemMapper.selectByExample(example);
        PromoItem promoItem2 = promoItems2.get(0);
        if (ret){
            response.setCode(PromoRetCode.SUCCESS.getCode());
            response.setMsg(PromoRetCode.SUCCESS.getMessage());
            response.setProductId(createPromoOrderRequest.getProductId());
            response.setInventory(promoItem2.getItemStock());
            return response;
        }

        response.setCode(PromoRetCode.SYSTEM_ERROR.getCode());
        response.setMsg(PromoRetCode.SYSTEM_ERROR.getMessage());
        return response;
    }
}
