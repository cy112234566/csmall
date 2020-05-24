package com.mall.shopping.services;

import com.mall.shopping.IProductCateService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.converter.ContentConverter;
import com.mall.shopping.converter.ProductCateConverter;
import com.mall.shopping.dal.entitys.ItemCat;
import com.mall.shopping.dal.persistence.ItemCatMapper;
import com.mall.shopping.dto.AllProductCateRequest;
import com.mall.shopping.dto.AllProductCateResponse;
import com.mall.shopping.dto.ProductCateDto;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;



/**
 * @author cy
 * @date 2020/5/13 14:28
 */
@Component
@Service
public class IProductCateServiceImpl implements IProductCateService {
    @Autowired
    private ItemCatMapper itemCatMapper;

    @Autowired
    private ProductCateConverter productCateConverter;
    @Override
    public AllProductCateResponse getAllProductCate(AllProductCateRequest request) {
        Example example = new Example(ItemCat.class);
        String sort = request.getSort();
        if (sort == null){
            example.setOrderByClause("sort_order ASC");
        }else {
            example.createCriteria().andEqualTo("sortOrder", Integer.valueOf(sort));
        }
        List<ItemCat> itemCats = itemCatMapper.selectByExample(example);
        List<ProductCateDto> productCateDtos = productCateConverter.items2Dto(itemCats);
        AllProductCateResponse response = new AllProductCateResponse();
        response.setProductCateDtoList(productCateDtos);
        response.setCode(ShoppingRetCode.SUCCESS.getCode());
        response.setMsg(ShoppingRetCode.SUCCESS.getMessage());
        return response;
    }
}
