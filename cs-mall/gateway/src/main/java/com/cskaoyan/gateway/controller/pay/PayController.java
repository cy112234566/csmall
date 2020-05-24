//package com.cskaoyan.gateway.controller.pay;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.cskaoyan.gateway.form.pay.PayForm;
//import com.mall.commons.result.ResponseData;
//import com.mall.commons.result.ResponseUtil;
//import com.mall.pay.PayService;
//import com.mall.pay.constant.PayRetCode;
//import com.mall.pay.dto.PrePayRequest;
//import com.mall.pay.dto.PrePayResponse;
//import com.mall.pay.dto.QueryPayRequest;
//import com.mall.pay.dto.QueryPayResponse;
//import com.mall.user.intercepter.TokenIntercepter;
//import io.swagger.annotations.Api;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.Reference;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//
//@Slf4j
//@RestController
//@RequestMapping("/payment")
//@Api(tags = "PayController", description = "支付层")
//public class PayController {
//
//    @Reference(timeout = 3000, check = false)
//    PayService payService;
//
//    @PostMapping("/cashier")
//    public ResponseData CreatePrePay(HttpServletRequest servletRequest, @RequestBody PayForm payForm) {
//        String userInfo = (String) servletRequest.getAttribute(TokenIntercepter.USER_INFO_KEY);
//        JSONObject object = JSON.parseObject(userInfo);
//        Long uid = Long.parseLong(object.get("uid").toString());
//        PrePayRequest request = PrePayRequest.builder()
//                .nickName(payForm.getNickName()).info(payForm.getInfo())
//                .orderId(payForm.getOrderId()).money(payForm.getMoney()).uid(uid)
//                .build();
//        PrePayResponse response = payService.createPrePay(request);
//        if (!response.getCode().equals(PayRetCode.SUCCESS.getCode()))
//            return new ResponseUtil<>().setErrorMsg(response.getMsg());
//        return new ResponseUtil<>().setData(response.getQRCodeUrl(), PayRetCode.PAIED.getMessage());
//    }
//
//    @GetMapping("/queryStatus")
//    public ResponseData queryPayStatus(HttpServletRequest servletRequest, String orderId) {
//        String userInfo = (String) servletRequest.getAttribute(TokenIntercepter.USER_INFO_KEY);
//        JSONObject object = JSON.parseObject(userInfo);
//        Long uid = Long.parseLong(object.get("uid").toString());
//        QueryPayRequest request = QueryPayRequest.builder()
//                .orderId(orderId).uid(uid)
//                .build();
//        QueryPayResponse response = payService.queryPayStatus(request);
//        if (!response.getCode().equals(PayRetCode.SUCCESS.getCode()))
//            return new ResponseUtil<>().setErrorMsg(response.getMsg());
//        return new ResponseUtil<>().setData(null);
//    }
//}
