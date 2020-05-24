package com.mall.order;

import com.mall.order.dto.CreateSeckillOrderRequest;
import com.mall.order.dto.CreateSeckillOrderResponse;

/**
 * @author cy
 * @date 2020/5/24 12:07
 */
public interface PromoOrderService {
    CreateSeckillOrderResponse createPromoOrder(CreateSeckillOrderRequest request);
}
