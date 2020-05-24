package com.mall.user;

import com.mall.user.dto.UserVerifyRequest;
import com.mall.user.dto.UserVerifyResponse;

/**
 * @Author zhanglonghao
 * @Date 2020/5/13 17:01
 * @Version 1.0
 */


public interface IUserVerifyService {

    UserVerifyResponse verify(UserVerifyRequest request);
}
