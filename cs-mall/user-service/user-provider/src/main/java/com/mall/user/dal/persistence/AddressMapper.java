package com.mall.user.dal.persistence;

import com.mall.commons.tool.tkmapper.TkMapper;
import com.mall.user.dal.entitys.Address;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressMapper extends TkMapper<Address> {
}
