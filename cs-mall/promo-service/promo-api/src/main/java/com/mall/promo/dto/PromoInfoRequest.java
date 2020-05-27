package com.mall.promo.dto;

import com.mall.commons.result.AbstractRequest;
import com.mall.commons.tool.exception.ValidateException;
import com.mall.promo.constants.PromoRetCode;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author cy
 * @date 2020/5/25 14:47
 */
@Data
public class PromoInfoRequest extends AbstractRequest {
    private Integer sessionId;

    private String yyyyMMdd;


    @Override
    public void requestCheck() {
        if (sessionId == null || StringUtils.isBlank(yyyyMMdd)){
            throw new ValidateException(PromoRetCode.REQUISITE_PARAMETER_NOT_EXIST.getCode(),PromoRetCode.REQUISITE_PARAMETER_NOT_EXIST.getMessage());
        }
    }
}
