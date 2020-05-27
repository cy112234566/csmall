package com.mall.promo.converter;

import com.mall.promo.dto.PromoItemInfoDto;
import com.mall.shopping.dto.ProductDetailDto;

/**
 * @author cy
 * @date 2020/5/25 15:40
 */
public class PromoItemConverter {

    public  PromoItemInfoDto productDetailDto2promoItemDto(ProductDetailDto productDetailDto){
        PromoItemInfoDto promoItemInfoDto = new PromoItemInfoDto();
        Integer productId = productDetailDto.getProductId().intValue();
        promoItemInfoDto.setId(productId);
        promoItemInfoDto.setProductName(productDetailDto.getProductName());
        promoItemInfoDto.setPrice(productDetailDto.getSalePrice());
        promoItemInfoDto.setPicUrl(productDetailDto.getProductImageBig());
        return promoItemInfoDto;
    }
}
