package com.mall.promo.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author cy
 * @date 2020/5/25 15:00
 */
@Data
public class PromoItemInfoDto {
    private Integer id;

    private Integer inventory;

    private BigDecimal price;

    private BigDecimal seckillPrice;

    private String picUrl;

    private String productName;
}
