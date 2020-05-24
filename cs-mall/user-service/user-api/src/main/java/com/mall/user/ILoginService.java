package com.mall.user;

import com.mall.user.dto.CheckAuthRequest;
import com.mall.user.dto.CheckAuthResponse;
import com.mall.user.dto.UserLoginRequest;
import com.mall.user.dto.UserLoginResponse;

/**
 * @Author zhanglonghao
 * @Date 2020/5/13 7:41
 * @Version 1.0
 */


public interface ILoginService {

    UserLoginResponse login(UserLoginRequest request);

    CheckAuthResponse validToken(CheckAuthRequest checkAuthRequest);
}
