package com.cskaoyan.gateway.controller.shopping;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.shopping.IContentService;
import com.mall.shopping.dto.NavListResponse;
import com.mall.user.annotation.Anoymous;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author cy
 * @date 2020/5/13 0:59
 */
@RestController
@RequestMapping("/shopping")
@Api(tags = "NavigationController",description = "导航栏控制层")
public class NavigationController {
    @Reference
    IContentService iContentService;

    @GetMapping("/navigation")
    @Anoymous
    public ResponseData navigation(){
        NavListResponse response = iContentService.queryNavList();
        return new ResponseUtil<>().setData(response.getPannelContentDtos());
    }
}
