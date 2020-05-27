package com.mall.promo.dto;

import com.mall.commons.result.AbstractRequest;
import com.mall.commons.tool.exception.ValidateException;
import com.mall.promo.constants.PromoRetCode;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @author cy
 * @date 2020/5/25 16:50
 */
@Data
public class CreatePromoOrderRequest extends AbstractRequest {

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 秒杀场次id
     */
    private Long psId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 商品秒杀价格
     */
    private BigDecimal promoPrice;

    @Override
    public void requestCheck() {
        if (productId == null || userId == null || StringUtils.isBlank(username) || psId == null){
            throw new ValidateException(PromoRetCode.REQUISITE_PARAMETER_NOT_EXIST.getCode(),PromoRetCode.REQUISITE_PARAMETER_NOT_EXIST.getMessage());
        }
    }
}
