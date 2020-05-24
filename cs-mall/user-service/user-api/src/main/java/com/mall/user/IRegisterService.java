package com.mall.user;

import com.mall.user.dto.UserRegisterRequest;
import com.mall.user.dto.UserRegisterResponse;

/**
 * @Author zhanglonghao
 * @Date 2020/5/11 0:09
 * @Version 1.0
 */


public interface IRegisterService {

    /**
     * 用戶註冊
     * @param registerRequest
     * @return
     */

    UserRegisterResponse register(UserRegisterRequest registerRequest);
}
