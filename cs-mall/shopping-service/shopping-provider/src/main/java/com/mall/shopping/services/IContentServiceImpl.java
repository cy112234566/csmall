package com.mall.shopping.services;


import com.mall.shopping.IContentService;
import com.mall.shopping.converter.ContentConverter;
import com.mall.shopping.dal.entitys.PanelContent;
import com.mall.shopping.dal.persistence.PanelContentMapper;
import com.mall.shopping.dto.NavListResponse;
import com.mall.shopping.dto.PanelContentDto;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;


import java.util.List;

/**
 * @author cy
 * @date 2020/5/13 13:58
 */
@Component
@Service
public class IContentServiceImpl implements IContentService {
    @Autowired
    private PanelContentMapper panelContentMapper;

    @Autowired
    private ContentConverter contentConverter;


    @Override
    public NavListResponse queryNavList() {
        Example example = new Example(PanelContent.class);
        example.createCriteria().andEqualTo("panelId",0);
        List<PanelContent> panelContents = panelContentMapper.selectByExample(example);
        List<PanelContentDto> panelContentDtos = contentConverter.panelContents2Dto(panelContents);
        NavListResponse navListResponse = new NavListResponse();
        navListResponse.setPannelContentDtos(panelContentDtos);
        return navListResponse;
    }
}
