package com.cskaoyan.gateway.controller.shopping;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.shopping.IHomeService;
import com.mall.shopping.dto.HomePageResponse;
import com.mall.user.annotation.Anoymous;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author cy
 * @date 2020/5/12 19:06
 */
@RestController
@RequestMapping("/shopping")
@Anoymous
@Api(tags = "HomePageController",description = "主页控制层")
public class HomePageController {

    @Reference
    IHomeService iHomeService;

    @GetMapping("/homepage")
    public ResponseData homepage(){
        HomePageResponse response = iHomeService.homepage();
        return new ResponseUtil<>().setData(response.getPanelContentItemDtos());
    }
}
