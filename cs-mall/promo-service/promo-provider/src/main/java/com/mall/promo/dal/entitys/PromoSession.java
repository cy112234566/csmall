package com.mall.promo.dal.entitys;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Table;
import java.util.Date;

/**
 * @author cy
 * @date 2020/5/25 12:57
 */
@Data
@ToString
@Table(name = "tb_promo_session")
public class PromoSession {
    private Integer id;

    //场次id 1：上午10点 2：下午4点
    private Integer sessionId;

    private Date startTime;

    private Date endTime;

    private String yyyymmdd;
}
