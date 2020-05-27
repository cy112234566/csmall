package com.mall.promo.dal.entitys;

import lombok.Data;
import lombok.ToString;


import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @author cy
 * @date 2020/5/25 12:51
 */
@Data
@ToString
@Table(name = "tb_promo_item")
public class PromoItem {

    private Integer id;

    //秒杀场次主键
    private Integer psId;

    private Integer itemId;

    private BigDecimal seckillPrice;

    private Integer itemStock;
}
