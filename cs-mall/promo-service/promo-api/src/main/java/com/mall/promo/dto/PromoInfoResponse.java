package com.mall.promo.dto;

import com.mall.commons.result.AbstractResponse;
import lombok.Data;

import java.util.List;

/**
 * @author cy
 * @date 2020/5/25 14:51
 */
@Data
public class PromoInfoResponse extends AbstractResponse {
    private Integer sessionId;

    private Integer psId;

    private List<PromoItemInfoDto> productList;
}
