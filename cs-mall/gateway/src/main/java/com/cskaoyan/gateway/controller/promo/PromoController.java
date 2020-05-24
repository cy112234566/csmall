package com.cskaoyan.gateway.controller.promo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cskaoyan.gateway.config.CacheManager;
import com.cskaoyan.gateway.form.promo.CreatePromoOrderInfo;
import com.google.common.util.concurrent.RateLimiter;
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
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.*;

@RestController
@RequestMapping("/shopping")
public class PromoController {

    @Reference(check = false)
    private PromoService promoService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private CacheManager cacheManager;

    private RateLimiter rateLimiter;

    private ExecutorService executorService;

    @PostConstruct
    public void init(){
        // 每秒产生100个令牌
        rateLimiter = RateLimiter.create(100);

        // 带有定时任务的线程池
//        executorService = Executors.newScheduledThreadPool();

        // 新建一个单线程的线程池
//        executorService = Executors.newSingleThreadExecutor();

        // 新建一个缓存类型的线程池
//        executorService = Executors.newCachedThreadPool();

        // 新建一个固定大小的线程池
        // LinkedBlockingQueue 无界的阻塞队列
        executorService = Executors.newFixedThreadPool(100);
    }

    @GetMapping("/seckilllist")
    @Anoymous
    public ResponseData getPromoList(@RequestParam Integer sessionId){
        PromoInfoRequest promoInfoRequest = new PromoInfoRequest();
        promoInfoRequest.setSessionId(sessionId);

        String yyyyMMdd = DateFormatUtils.format(new Date(), "yyyyMMdd");
        promoInfoRequest.setYyyymmdd(yyyyMMdd);
        PromoInfoResponse promoInfoResponse = promoService.getPromoList(promoInfoRequest);
        if (!promoInfoResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())){
            return new ResponseUtil<>().setErrorMsg(promoInfoResponse.getMsg());
        }
        return new ResponseUtil<>().setData(promoInfoResponse);
    }

    @PostMapping("/seckill")
    public ResponseData seckill(HttpServletRequest request, @RequestBody CreatePromoOrderInfo createPromoOrderInfo){

        // 获取一个令牌，返回值可以理解为等待时间
        /**
         * public double acquire(int permits) {
         *         // 需要等待的时间
         *         long microsToWait = this.reserve(permits);
         *         // 让线程去等待
         *         this.stopwatch.sleepMicrosUninterruptibly(microsToWait);
         *         return 1.0D * (double)microsToWait / (double)TimeUnit.SECONDS.toMicros(1L);
         *     }
         */
        rateLimiter.acquire();


        String userInfo = (String) request.getAttribute(TokenIntercepter.USER_INFO_KEY);
        JSONObject jsonObject = JSON.parseObject(userInfo);
        String username = (String) jsonObject.get("username");
        Long uid = Long.parseLong((String) jsonObject.get("uid"));

        CreatePromoOrderRequest createPromoOrderRequest = new CreatePromoOrderRequest();
        createPromoOrderRequest.setProductId(createPromoOrderInfo.getProductId());
        createPromoOrderRequest.setUserId(uid);
        createPromoOrderRequest.setUsername(username);

        /**
         * 在redis中查看有无库存售罄的标记
         * 有，表示已售罄
         * 无，表示未售罄
         */
        String key = "promo_item_stock_not_enough_" + createPromoOrderInfo.getPsId() + "_" + createPromoOrderInfo.getProductId();
        String cache = cacheManager.checkCache(key);
        if (!StringUtils.isBlank(cache)){
            return new ResponseUtil<>().setErrorMsg("库存已经售罄");
        }

        // 通过线程池限制派发给下游的秒杀服务的流量
        // 线程池主要作用是保护下游系统
        Future<CreatePromoOrderResponse> future = executorService.submit(new Callable<CreatePromoOrderResponse>() {
            @Override
            public CreatePromoOrderResponse call() throws Exception {
                CreatePromoOrderResponse promoOrderResponse = promoService.createPromoOrderInTransaction(createPromoOrderRequest);
                return promoOrderResponse;
            }
        });

        CreatePromoOrderResponse promoOrderResponse = null;
        try {
            promoOrderResponse = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

//        CreatePromoOrderResponse promoOrderResponse = promoService.createPromoOrderInTransaction(createPromoOrderRequest);

        if (!promoOrderResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())){
            return new ResponseUtil<>().setErrorMsg(promoOrderResponse.getMsg());
        }

        return new ResponseUtil<>().setData(promoOrderResponse);
    }

}
