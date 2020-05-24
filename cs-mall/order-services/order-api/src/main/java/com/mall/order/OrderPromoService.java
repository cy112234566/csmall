package com.mall.order;

import com.mall.order.dto.CreateSeckillOrderRequest;
import com.mall.order.dto.CreateSeckillOrderResponse;

public interface OrderPromoService {

    /**
     * 创建秒杀订单
     * @param request
     * @return
     */
    CreateSeckillOrderResponse createPromoOrder(CreateSeckillOrderRequest request);
}
