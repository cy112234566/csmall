package com.cskaoyan.gateway.controller.shopping;

/**
 * @author cy
 * @date 2020/5/24 18:39
 */
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.order.OrderCoreService;
import com.mall.order.OrderQueryService;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.dto.*;
import com.mall.user.annotation.Anoymous;
import com.mall.user.intercepter.TokenIntercepter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/shopping")
@Api(tags = "OrderController", description = "订单控制层")
public class OrderController {

    @Reference(timeout = 3000, check = false)
    private OrderCoreService orderCoreService;

    @Reference(timeout = 3000, check = false)
    private OrderQueryService orderQueryService;

    /**
     * 创建订单
     */
    @PostMapping("/order")
    @ApiOperation("创建订单")
    public ResponseData order(@RequestBody CreateOrderRequest request, HttpServletRequest servletRequest){
        String userInfo = (String)servletRequest.getAttribute(TokenIntercepter.USER_INFO_KEY);
        JSONObject object = JSON.parseObject(userInfo);
        Long uid = Long.parseLong(object.get("uid").toString());

        request.setUserId(uid);

        // 设置uniqueKey
        request.setUniqueKey(UUID.randomUUID().toString());
        CreateOrderResponse response = orderCoreService.createOrder(request);
        if (response.getCode().equals(OrderRetCode.SUCCESS.getCode())){
            return new ResponseUtil<>().setData(response.getOrderId());
        }
        return new ResponseUtil<>().setErrorMsg(response.getMsg());
    }

    /**
     * 获取当前用户的所有订单
     * @param
     * @param servletRequest
     * @return
     */
    @GetMapping("/order")
    @ApiOperation("获取当前用户的所有订单")
    public ResponseData queryAllOrders(@RequestParam("page") Integer page,@RequestParam("size") Integer size , HttpServletRequest servletRequest){
        String userInfo = (String)servletRequest.getAttribute(TokenIntercepter.USER_INFO_KEY);
        JSONObject object = JSON.parseObject(userInfo);
        Long uid = Long.parseLong(object.get("uid").toString());
        OrderListRequest request = new OrderListRequest();
        request.setPage(page);
        request.setSize(size);
        request.setUserId(uid);
        OrderListResponse response = orderQueryService.queryAllOrders(request);
        if (!response.getCode().equals(OrderRetCode.SUCCESS.getCode())){
            return new ResponseUtil<>().setErrorMsg(response.getMsg());
        }
        HashMap<String,Object> map = new HashMap<>();
        map.put("data",response.getDetailInfoList());
        map.put("total",response.getTotal());
        return new ResponseUtil<>().setData(map);
    }

    /**
     * 查询订单详情
     * @param id
     * @param servletRequest
     * @return
     */
    @GetMapping("/order/{id}")
    @ApiOperation("查询订单详情")
    public ResponseData queryOrder(@PathVariable("id") String id, HttpServletRequest servletRequest){
        String userInfo = (String)servletRequest.getAttribute(TokenIntercepter.USER_INFO_KEY);
        JSONObject object = JSON.parseObject(userInfo);
        Long uid = Long.parseLong(object.get("uid").toString());

        OrderDetailRequest2 request = new OrderDetailRequest2();
        request.setUserId(uid);
        request.setOrderId(id);
//        request.setOrderItemId(String.valueOf(pid));

        OrderDetailResponse2 response = orderQueryService.queryOrder(request);
        if (response.getCode().equals(OrderRetCode.SUCCESS.getCode())){
            return new ResponseUtil<>().setData(response);
        }
        return new ResponseUtil<>().setErrorMsg(response.getMsg());
    }

    /**
     * 取消订单
     * @param id
     * @param servletRequest
     * @return
     */
    @PutMapping("/order/{id}")
    @ApiOperation("取消订单")
    public ResponseData cancelOrder(@PathVariable("id") String id, HttpServletRequest servletRequest){
        CancelOrderRequest request = new CancelOrderRequest();
        request.setOrderId(id);
        CancelOrderResponse response = orderCoreService.cancelOrder(request);
        if (response.getCode().equals(OrderRetCode.SUCCESS.getCode())){
            return new ResponseUtil<>().setData("成功");
        }
        return new ResponseUtil<>().setErrorMsg(response.getMsg());
    }

    /**
     * 删除订单
     * @param id
     * @param servletRequest
     * @return
     */
    @DeleteMapping("/order/{id}")
    @ApiOperation("删除订单")
    public ResponseData deleteOrder(@PathVariable("id") String id, HttpServletRequest servletRequest){
        DeleteOrderRequest request = new DeleteOrderRequest();
        request.setOrderId(id);
        DeleteOrderResponse response = orderCoreService.deleteOrder(request);
        if (response.getCode().equals(OrderRetCode.SUCCESS.getCode())){
            return new ResponseUtil<>().setData("成功");
        }
        return new ResponseUtil<>().setErrorMsg(response.getMsg());
    }

}
