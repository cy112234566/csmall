package com.mall.user.dal.persistence;

import com.mall.commons.tool.tkmapper.TkMapper;
import com.mall.user.dal.entitys.Member;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface MemberMapper extends TkMapper<Member> {
}
