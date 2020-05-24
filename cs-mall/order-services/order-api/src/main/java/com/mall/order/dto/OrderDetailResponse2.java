package com.mall.order.dto;

import com.mall.commons.result.AbstractResponse;
import lombok.Data;

import java.util.List;

@Data
public class OrderDetailResponse2 extends AbstractResponse {
    private String userName;

    private Long orderTotal;

    private Long userId;

    private List<OrderItemDto> goodsList;

    private String tel;

    private String streetName;
}
