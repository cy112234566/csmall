package com.cskaoyan.gateway.controller.shopping;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.shopping.IProductCateService;
import com.mall.shopping.dto.AllProductCateRequest;
import com.mall.shopping.dto.AllProductCateResponse;
import com.mall.user.annotation.Anoymous;
import io.swagger.annotations.Api;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author cy
 * @date 2020/5/13 14:15
 */
@RestController
@RequestMapping("/shopping")
@Api(tags = "CategoriesController",description = "商品种类控制层")
public class CategoriesController {
    @Reference
    IProductCateService iProductCateService;

    @GetMapping("/categories")
    @Anoymous
    public ResponseData categories(@RequestParam(value = "sort",required = false) String sort){
        AllProductCateRequest cateRequest = new AllProductCateRequest();
        cateRequest.setSort(sort);
        AllProductCateResponse allProductCate = iProductCateService.getAllProductCate(cateRequest);
        return new ResponseUtil<>().setData(allProductCate.getProductCateDtoList());
    }
}
