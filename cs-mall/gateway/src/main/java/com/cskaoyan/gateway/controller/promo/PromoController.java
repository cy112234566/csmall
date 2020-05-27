package com.cskaoyan.gateway.controller.promo;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cskaoyan.gateway.cache.CacheManager;
import com.cskaoyan.gateway.form.promo.CreatePromoOrderInfo;
import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.promo.PromoService;
import com.mall.promo.dto.CreatePromoOrderRequest;
import com.mall.promo.dto.CreatePromoOrderResponse;
import com.mall.promo.dto.PromoInfoRequest;
import com.mall.promo.dto.PromoInfoResponse;
import com.mall.user.annotation.Anoymous;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.intercepter.TokenIntercepter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.Date;

@RestController
@RequestMapping("/shopping")
public class PromoController {
    @Reference(check = false)
    PromoService promoService;

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/seckilllist")
    @Anoymous
    public ResponseData getPromoList(@RequestParam Integer sessionId){
        PromoInfoRequest promoInfoRequest = new PromoInfoRequest();
        promoInfoRequest.setSessionId(sessionId);
        String yyyyMMdd = DateFormatUtils.format(new Date(), "yyyyMMdd");
        promoInfoRequest.setYyyyMMdd(yyyyMMdd);
        PromoInfoResponse promoList = promoService.getPromoList(promoInfoRequest);
        if (!promoList.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())){
            return new ResponseUtil<>().setErrorMsg(promoList.getMsg());
        }
        return new ResponseUtil<>().setData(promoList);
    }


    @PostMapping("/seckill")
    public ResponseData createPromoOrder(HttpServletRequest request, @RequestBody CreatePromoOrderInfo createPromoOrderInfo){
        String userInfo = (String) request.getAttribute(TokenIntercepter.USER_INFO_KEY);
        JSONObject jsonObject = JSON.parseObject(userInfo);
        String username = (String) jsonObject.get("username");
        Long uid = Long.parseLong((String) jsonObject.get("uid"));
        CreatePromoOrderRequest createPromoOrderRequest = new CreatePromoOrderRequest();
        createPromoOrderRequest.setProductId(createPromoOrderInfo.getProductId());
        createPromoOrderRequest.setPsId(createPromoOrderInfo.getPsId());
        createPromoOrderRequest.setUserId(uid);
        createPromoOrderRequest.setUsername(username);
        /**
         * 这里可以去redis里看一下  商品是否售罄
         * 如果已售罄  后面的代码不用执行
         * 如果未售罄  正常下单
         */
        String key = "promo_item_stock_not_enough_"+createPromoOrderRequest.getPsId()+"_"+createPromoOrderRequest.getProductId();
        String value = cacheManager.checkCache(key);
        if (!StringUtils.isBlank(value)){
            return new ResponseUtil<>().setErrorMsg("商品已售罄");
        }
        CreatePromoOrderResponse createPromoOrderResponse = promoService.createPromoOrderInTransaction(createPromoOrderRequest);
        if (!createPromoOrderResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())){
            return new ResponseUtil<>().setErrorMsg(createPromoOrderResponse.getMsg());
        }
        return new ResponseUtil<>().setData(createPromoOrderResponse);
    }


}
