package com.mall.promo;


import com.mall.promo.dto.CreatePromoOrderRequest;
import com.mall.promo.dto.CreatePromoOrderResponse;
import com.mall.promo.dto.PromoInfoRequest;
import com.mall.promo.dto.PromoInfoResponse;

/**
 * @author cy
 * @date 2020/5/25 14:48
 */
public interface PromoService {
    /**
     * 获取秒杀商品接口
     * @param promoInfoRequest
     * @return
     */
    PromoInfoResponse getPromoList(PromoInfoRequest promoInfoRequest);


    /**
     * 创建秒杀订单接口
     * @param createPromoOrderRequest
     * @return
     */
    CreatePromoOrderResponse createPromoOrder(CreatePromoOrderRequest createPromoOrderRequest);

    /**
     * 分布式事务控制的创建秒杀订单接口
     * @param createPromoOrderRequest
     * @return
     */
    CreatePromoOrderResponse createPromoOrderInTransaction(CreatePromoOrderRequest createPromoOrderRequest);
}
