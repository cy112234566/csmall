package com.mall.shopping.services;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mall.shopping.IProductService;
import com.mall.shopping.converter.ContentConverter;
import com.mall.shopping.converter.ProductConverter;
import com.mall.shopping.dal.entitys.Item;
import com.mall.shopping.dal.entitys.ItemDesc;
import com.mall.shopping.dal.entitys.PanelContentItem;
import com.mall.shopping.dal.persistence.ItemDescMapper;
import com.mall.shopping.dal.persistence.ItemMapper;
import com.mall.shopping.dal.persistence.PanelContentMapper;
import com.mall.shopping.dto.*;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author cy
 * @date 2020/5/13 16:42
 */
@Component
@Service
public class IProductServiceImpl implements IProductService {
    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ItemDescMapper itemDescMapper;

    @Autowired
    private PanelContentMapper panelContentMapper;

    @Autowired
    private ProductConverter productConverter;

    @Autowired
    private ContentConverter contentConverter;
    @Override
    public ProductDetailResponse getProductDetail(ProductDetailRequest request) {
        ProductDetailResponse productDetailResponse = new ProductDetailResponse();
        Item item = itemMapper.selectByPrimaryKey(request.getId());
        Example example = new Example(ItemDesc.class);
        example.createCriteria().andEqualTo("itemId",item.getId());
        List<ItemDesc> itemDescs = itemDescMapper.selectByExample(example);
        List<String> productImage = new ArrayList<>();
        String[] itemImages = item.getImages();
        for (String itemImage : itemImages) {
            productImage.add(itemImage);
        }
        ProductDetailDto productDetailDto = new ProductDetailDto();
        for (ItemDesc itemDesc : itemDescs) {
            productDetailDto.setDetail(itemDesc.getItemDesc());
        }

        productDetailDto.setProductId(item.getId());
        productDetailDto.setLimitNum(Long.valueOf(item.getLimitNum()));
        productDetailDto.setProductImageBig(item.getImageBig());
        productDetailDto.setProductName(item.getTitle());
        productDetailDto.setSalePrice(item.getPrice());
        productDetailDto.setSubTitle(item.getSellPoint());
        productDetailDto.setProductImageSmall(productImage);
        productDetailResponse.setProductDetailDto(productDetailDto);
        return productDetailResponse;
    }

    @Override
    public AllProductResponse getAllProduct(AllProductRequest request) {
        PageHelper.startPage(request.getPage(), request.getSize());
        AllProductResponse allProductResponse = new AllProductResponse();
        String sort = request.getSort();
        String orderCol ;
        String orderDir ;
        if (sort == null){
             orderCol = "id";
             orderDir = "ASC";
        }else if (sort.equals("1")){
             orderCol = "price";
             orderDir = "ASC";
        }else {
            orderCol = "price";
            orderDir = "DESC";
        }
        List<Item> items = itemMapper.selectItemFront(request.getCid(),orderCol ,orderDir, request.getPriceGt(), request.getPriceLte());
        List<ProductDto> productDtos = productConverter.items2Dto(items);
        PageInfo<ProductDto> itemPageInfo = new PageInfo<>(productDtos);
        long total = itemPageInfo.getTotal();
        allProductResponse.setProductDtoList(productDtos);
        allProductResponse.setTotal(total);
        return allProductResponse;
    }

    @Override
    public RecommendResponse getRecommendGoods() {
        RecommendResponse response = new RecommendResponse();
        Set<PanelDto> panelDtos = new HashSet<PanelDto>();
        Integer panelId = 6;
        List<PanelContentItem> panelContents = panelContentMapper.selectPanelContentAndProductWithPanelId(panelId);
        List<PanelContentItemDto> panelContentItemDtos = contentConverter.panelContentItem2Dto(panelContents);
        PanelDto panelDto = new PanelDto();
        panelDto.setPanelContentItems(panelContentItemDtos);
        panelDtos.add(panelDto);
        response.setPanelContentItemDtos(panelDtos);
        return response;
    }
}
