package com.mall.shopping.services;

import com.mall.shopping.IHomeService;
import com.mall.shopping.converter.ContentConverter;
import com.mall.shopping.dal.entitys.Panel;
import com.mall.shopping.dal.entitys.PanelContent;
import com.mall.shopping.dal.entitys.PanelContentItem;
import com.mall.shopping.dal.persistence.PanelContentMapper;
import com.mall.shopping.dal.persistence.PanelMapper;
import com.mall.shopping.dto.HomePageResponse;
import com.mall.shopping.dto.PanelContentItemDto;
import com.mall.shopping.dto.PanelDto;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author cy
 * @date 2020/5/13 0:04
 */
@Component
@Service
public class IHomeServiceImpl implements IHomeService {
    @Autowired
    private PanelMapper panelMapper;
    
    @Autowired
    private PanelContentMapper panelContentMapper;

    @Autowired
    private ContentConverter contentConverter;

    @Override
    public HomePageResponse homepage() {
        HomePageResponse homePageResponse = new HomePageResponse();
        List<Panel> panelList = panelMapper.selectAll();
        Set<PanelDto> panelDtos = new HashSet<PanelDto>();
        for (Panel panel : panelList) {
            Integer panelId = panel.getId();
            PanelDto panelDto = contentConverter.panen2Dto(panel);
            List<PanelContentItem> panelContents = panelContentMapper.selectPanelContentAndProductWithPanelId(panelId);
            List<PanelContentItemDto> panelContentItemDtos = contentConverter.panelContentItem2Dto(panelContents);
            panelDto.setPanelContentItems(panelContentItemDtos);
            panelDtos.add(panelDto);
        }
        homePageResponse.setPanelContentItemDtos(panelDtos);
        return homePageResponse;
    }
}
