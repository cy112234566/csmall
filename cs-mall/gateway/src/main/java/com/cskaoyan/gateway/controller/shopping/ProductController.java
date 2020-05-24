package com.cskaoyan.gateway.controller.shopping;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.shopping.IProductService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.dto.*;

import com.mall.user.annotation.Anoymous;
import io.swagger.annotations.ApiImplicitParam;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


/**
 * @author cy
 * @date 2020/5/13 15:46
 */
@RestController
@RequestMapping("/shopping")
@Anoymous
public class ProductController {
    @Reference
    IProductService iProductService;

    @GetMapping("/product/{id}")
    @ApiImplicitParam(name = "id",value = "商品ID",paramType = "path",dataType = "Long")
    public ResponseData product(@PathVariable(name = "id") String id){
        ProductDetailRequest productDetailRequest = new ProductDetailRequest();
        Long ID;
        if (id == null){
            return new ResponseUtil<>().setErrorMsg(ShoppingRetCode.REQUISITE_PARAMETER_NOT_EXIST.getMessage());
        }else {
         ID = Long.parseLong(id);}
        productDetailRequest.setId(ID);
        ProductDetailResponse productDetail = iProductService.getProductDetail(productDetailRequest);
        return new ResponseUtil<>().setData(productDetail.getProductDetailDto());
    }

    @GetMapping("/goods")
    public ResponseData goods(@RequestParam("page") Integer page,@RequestParam("size") Integer size,@RequestParam(value = "sort",required = false) String sort,@RequestParam(value = "priceGt",required = false) Integer priceGt,@RequestParam(value = "priceLte",required = false) Integer priceLte,@RequestParam(value = "cid",required = false) Long cid){
        AllProductRequest allProductRequest = new AllProductRequest();
        allProductRequest.setCid(cid);
        allProductRequest.setPage(page);
        allProductRequest.setSize(size);
        allProductRequest.setSort(sort);
        allProductRequest.setPriceGt(priceGt);
        allProductRequest.setPriceLte(priceLte);
        AllProductResponse allProduct = iProductService.getAllProduct(allProductRequest);
        Map<Object,Object> map = new HashMap<>();
        map.put("data",allProduct.getProductDtoList());
        map.put("total",allProduct.getTotal());
        return new ResponseUtil<>().setData(map);
    }

    @GetMapping("/recommend")
    public ResponseData recommend(){
        RecommendResponse recommendGoods = iProductService.getRecommendGoods();
        return new ResponseUtil<>().setData(recommendGoods.getPanelContentItemDtos());
    }
}
