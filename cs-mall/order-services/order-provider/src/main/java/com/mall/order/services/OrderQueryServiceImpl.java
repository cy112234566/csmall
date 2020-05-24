package com.mall.order.services;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.order.OrderQueryService;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.converter.OrderConverter;
import com.mall.order.dal.entitys.*;
import com.mall.order.dal.persistence.OrderItemMapper;
import com.mall.order.dal.persistence.OrderMapper;
import com.mall.order.dal.persistence.OrderShippingMapper;
import com.mall.order.dto.*;
import com.mall.order.utils.ExceptionProcessorUtils;
import com.mall.user.constants.SysRetCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *  ciggar
 * create-date: 2019/7/30-上午10:04
 */
@Slf4j
@Component
@Service
public class OrderQueryServiceImpl implements OrderQueryService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderShippingMapper orderShippingMapper;

    @Override
    public OrderListResponse queryAllOrders(OrderListRequest request) {
        OrderListResponse orderListResponse = new OrderListResponse();
        request.requestCheck();

        PageHelper.startPage(request.getPage(),request.getSize());
//        PageHelper.orderBy(request.getSort());

        Example example = new Example(Order.class);
        example.createCriteria().andEqualTo("userId",request.getUserId());
        List<Order> orderList = orderMapper.selectByExample(example);

        orderListResponse.setTotal(PageInfo.of(orderList).getTotal());

        List<OrderDetailInfo> orderDetailInfos = new ArrayList<>();
        for (Order order : orderList) {
            OrderDetailInfo orderDetailInfo = new OrderDetailInfo();
            orderDetailInfo.setOrderId(order.getOrderId());
            orderDetailInfo.setPayment(order.getPayment());
            orderDetailInfo.setPaymentType(order.getPaymentType());
            orderDetailInfo.setPostFee(order.getPostFee());
            orderDetailInfo.setStatus(order.getStatus());
            orderDetailInfo.setCreateTime(order.getCreateTime());
            orderDetailInfo.setUpdateTime(order.getUpdateTime());
            orderDetailInfo.setPaymentTime(order.getPaymentTime());
            orderDetailInfo.setConsignTime(order.getConsignTime());
            orderDetailInfo.setEndTime(order.getEndTime());
            orderDetailInfo.setCloseTime(order.getCloseTime());
            orderDetailInfo.setShippingName(order.getShippingName());
            orderDetailInfo.setShippingCode(order.getShippingCode());
            orderDetailInfo.setUserId(order.getUserId());
            orderDetailInfo.setBuyerMessage(order.getBuyerMessage());
            orderDetailInfo.setBuyerNick(order.getBuyerNick());
            orderDetailInfo.setBuyerComment(order.getBuyerComment());

            // 根据订单号查商品
            List<OrderItemDto> itemDtos = new ArrayList<>();

            Example example2 = new Example(OrderItem.class);
            example2.createCriteria().andEqualTo("orderId",order.getOrderId());
            List<OrderItem> orderItems = orderItemMapper.selectByExample(example2);
            for (OrderItem orderItem : orderItems) {
                OrderItemDto orderItemDto = new OrderItemDto();
                orderItemDto.setId(orderItem.getId());
                orderItemDto.setItemId(String.valueOf(orderItem.getItemId()));
                orderItemDto.setOrderId(orderItem.getOrderId());
                orderItemDto.setNum(orderItem.getNum());
                orderItemDto.setTitle(orderItem.getTitle());
                orderItemDto.setPrice(new BigDecimal(orderItem.getPrice()));
                orderItemDto.setTotalFee(new BigDecimal(orderItem.getTotalFee()));
                orderItemDto.setPicPath(orderItem.getPicPath());
                itemDtos.add(orderItemDto);
            }
            orderDetailInfo.setOrderItemDto(itemDtos);

            // 根据订单号查物流信息
            OrderShippingDto orderShippingDto = new OrderShippingDto();
            Example example3 = new Example(OrderShipping.class);
            example3.createCriteria().andEqualTo("orderId",order.getOrderId());
            List<OrderShipping> orderShippings = orderShippingMapper.selectByExample(example3);
            OrderShipping orderShipping = orderShippings.get(0);
            orderShippingDto.setOrderId(orderShipping.getOrderId());
            orderShippingDto.setReceiverName(orderShipping.getReceiverName());
            orderShippingDto.setReceiverPhone(orderShipping.getReceiverPhone());
            orderShippingDto.setReceiverMobile(orderShipping.getReceiverMobile());
            orderShippingDto.setReceiverState(orderShipping.getReceiverState());
            orderShippingDto.setReceiverCity(orderShipping.getReceiverCity());
            orderShippingDto.setReceiverDistrict(orderShipping.getReceiverDistrict());
            orderShippingDto.setReceiverAddress(orderShipping.getReceiverAddress());
            orderShippingDto.setReceiverZip(orderShipping.getReceiverZip());

            orderDetailInfo.setOrderShippingDto(orderShippingDto);

            orderDetailInfos.add(orderDetailInfo);
        }
        orderListResponse.setDetailInfoList(orderDetailInfos);

//        long total = page.getTotal();
//        orderListResponse.setTotal(total);
        orderListResponse.setCode(SysRetCodeConstants.SUCCESS.getCode());
        orderListResponse.setMsg(SysRetCodeConstants.SUCCESS.getMessage());
        return orderListResponse;
    }

    @Override
    public OrderDetailResponse2 queryOrder(OrderDetailRequest2 request) {
        request.requestCheck();
        OrderDetailResponse2 response = new OrderDetailResponse2();
        Example example = new Example(Order.class);
        example.createCriteria().andEqualTo("orderId", request.getOrderId());
        List<Order> orders = orderMapper.selectByExample(example);
        Order orderForQuery = orders.get(0);
        response.setOrderTotal(orderForQuery.getPayment().longValue());

        Order order = new Order();
        order.setOrderId(request.getOrderId());
        OrderShipping orderShipping = orderShippingMapper.selectByPrimaryKey(order);
        response.setUserName(orderShipping.getReceiverName());
        response.setUserId(request.getUserId());
        response.setTel(orderShipping.getReceiverPhone());
        response.setStreetName(orderShipping.getReceiverAddress());

        Example example2 = new Example(OrderItem.class);
        example2.createCriteria().andEqualTo("orderId",request.getOrderId());

        List<OrderItem> orderItems = orderItemMapper.selectByExample(example2);
        List<OrderItemDto> itemDtos = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            OrderItemDto orderItemDto = new OrderItemDto();
            orderItemDto.setId(orderItem.getId());
            orderItemDto.setItemId(String.valueOf(orderItem.getItemId()));
            orderItemDto.setOrderId(orderItem.getOrderId());
            orderItemDto.setNum(orderItem.getNum());
            orderItemDto.setTitle(orderItem.getTitle());
            orderItemDto.setPrice(new BigDecimal(orderItem.getPrice()));
            orderItemDto.setTotalFee(new BigDecimal(orderItem.getTotalFee()));
            orderItemDto.setPicPath(orderItem.getPicPath());
            itemDtos.add(orderItemDto);
        }
        response.setGoodsList(itemDtos);
        response.setCode(OrderRetCode.SUCCESS.getCode());
        response.setMsg(OrderRetCode.SUCCESS.getMessage());
        return response;
    }

    @Override
    public boolean checkPayStatus(String orderId) {
        Example example = new Example(Order.class);
        example.createCriteria().andEqualTo(orderId).andIsNotNull("paymentTime");
        return orderMapper.selectCountByExample(example) > 0;
    }
}
