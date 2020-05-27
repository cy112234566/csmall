package com.mall.promo.dto;

import com.mall.commons.result.AbstractResponse;
import lombok.Data;

/**
 * @author cy
 * @date 2020/5/25 16:54
 */
@Data
public class CreatePromoOrderResponse extends AbstractResponse {

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品剩余库存
     */
    private Integer inventory;
}
