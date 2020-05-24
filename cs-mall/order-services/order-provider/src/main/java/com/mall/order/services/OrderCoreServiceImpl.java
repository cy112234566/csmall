package com.mall.order.services;

import com.mall.commons.tool.exception.BizException;
import com.mall.order.OrderCoreService;
import com.mall.order.biz.TransOutboundInvoker;
import com.mall.order.biz.context.AbsTransHandlerContext;
import com.mall.order.biz.factory.OrderProcessPipelineFactory;
import com.mall.order.constant.OrderRetCode;
import com.mall.order.constants.OrderConstants;
import com.mall.order.dal.entitys.Order;
import com.mall.order.dal.entitys.OrderItem;
import com.mall.order.dal.entitys.Stock;
import com.mall.order.dal.persistence.OrderItemMapper;
import com.mall.order.dal.persistence.OrderMapper;
import com.mall.order.dal.persistence.OrderShippingMapper;
import com.mall.order.dal.persistence.StockMapper;
import com.mall.order.dto.*;
import com.mall.order.utils.ExceptionProcessorUtils;
import com.mall.user.constants.SysRetCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *  ciggar
 * create-date: 2019/7/30-上午10:05
 */
@Slf4j
@Component
@Service(cluster = "failfast")
public class OrderCoreServiceImpl implements OrderCoreService {

	@Autowired
	OrderMapper orderMapper;

	@Autowired
	OrderItemMapper orderItemMapper;

	@Autowired
	OrderShippingMapper orderShippingMapper;

	@Autowired
    OrderProcessPipelineFactory orderProcessPipelineFactory;

	@Autowired
	StockMapper stockMapper;


	/**
	 * 创建订单的处理流程
	 *
	 * @param request
	 * @return
	 */
	@Override
	public CreateOrderResponse createOrder(CreateOrderRequest request) {
		CreateOrderResponse response = new CreateOrderResponse();
		try {
			//创建pipeline对象
			TransOutboundInvoker invoker = orderProcessPipelineFactory.build(request);

			//启动pipeline
			invoker.start(); //启动流程（pipeline来处理）

			//获取处理结果
			AbsTransHandlerContext context = invoker.getContext();

			//把处理结果转换为response
			response = (CreateOrderResponse) context.getConvert().convertCtx2Respond(context);
		} catch (Exception e) {
			log.error("OrderCoreServiceImpl.createOrder Occur Exception :" + e);
			ExceptionProcessorUtils.wrapperHandlerException(response, e);
		}
		return response;
	}

	/**
	 * 取消订单
	 * @param request
	 * @return
	 */
	@Override
	public CancelOrderResponse cancelOrder(CancelOrderRequest request) {
		CancelOrderResponse response = new CancelOrderResponse();
		request.requestCheck();
		String orderId = request.getOrderId();
		Order order = new Order();
		order.setOrderId(orderId);
		order.setStatus(OrderConstants.ORDER_STATUS_TRANSACTION_CANCEL);
		order.setUpdateTime(new Date());
		order.setCloseTime(new Date());
		order.setEndTime(new Date());
		int effectedRows = orderMapper.updateByPrimaryKey(order);
		if (effectedRows < 1){
			throw new BizException(OrderRetCode.DB_EXCEPTION.getCode(),OrderRetCode.DB_EXCEPTION.getMessage());
		}

		Example example = new Example(OrderItem.class);
		example.createCriteria().andEqualTo("orderId",orderId);
		List<OrderItem> orderItems = orderItemMapper.selectByExample(example);

		// 锁定库存
		List<Long> productIds = new ArrayList<>();
		for (OrderItem item : orderItems) {
			productIds.add(item.getItemId());
		}
		List<Stock> stockList = stockMapper.findStocksForUpdate(productIds);

		// 库存增加
		for (OrderItem orderItem : orderItems) {
			Long num = orderItem.getNum().longValue();
			Long itemId = orderItem.getItemId();

			Stock stock = new Stock();
			stock.setItemId(itemId);
			stock.setLockCount(num.intValue());
			stock.setStockCount(num);
			stockMapper.updateStock(stock);
		}

		response.setCode(OrderRetCode.SUCCESS.getCode());
		response.setMsg(OrderRetCode.SUCCESS.getMessage());
		return response;
	}

	/**
	 * 删除订单
	 * @param request
	 * @return
	 */
	@Override
	public DeleteOrderResponse deleteOrder(DeleteOrderRequest request) {
		request.requestCheck();
		DeleteOrderResponse response = new DeleteOrderResponse();
		String orderId = request.getOrderId();
		Order order = new Order();
		order.setOrderId(orderId);
		int effectedRows = orderMapper.deleteByPrimaryKey(order);
		if (effectedRows < 1){
			throw new BizException(OrderRetCode.DB_EXCEPTION.getCode(),OrderRetCode.DB_EXCEPTION.getMessage());
		}
		response.setCode(OrderRetCode.SUCCESS.getCode());
		response.setMsg(OrderRetCode.SUCCESS.getMessage());
		return response;
	}

}
