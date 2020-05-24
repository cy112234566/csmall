package com.cskaoyan.gateway.controller.user;

import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.user.IUserVerifyService;
import com.mall.user.annotation.Anoymous;
import com.mall.user.constants.SysRetCodeConstants;
import com.mall.user.dto.UserVerifyRequest;
import com.mall.user.dto.UserVerifyResponse;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author zhanglonghao
 * @Date 2020/5/13 17:03
 * @Version 1.0
 */

@RestController
@RequestMapping("/user")
public class UserVerifyController {

    @Reference(check = false)
    private IUserVerifyService userVerifyService;

    @GetMapping("/verify")
    @Anoymous
    public ResponseData verify(@RequestParam String uid, @RequestParam String username, HttpServletRequest request, HttpServletResponse response){
        //验证
        if(StringUtils.isEmpty(uid) || StringUtils.isEmpty(username)){
            return new ResponseUtil<>().setErrorMsg("用戶名或密碼不能為空");
        }
        UserVerifyRequest userVerifyRequest = new UserVerifyRequest();
        userVerifyRequest.setUserName(username);
        userVerifyRequest.setUuid(uid);
        UserVerifyResponse userVerifyResponse = new UserVerifyResponse();

        if (userVerifyResponse.getCode().equals(SysRetCodeConstants.SUCCESS.getCode())){
            return new ResponseUtil<>().setData(null);
        }

        return new ResponseUtil<>().setErrorMsg(userVerifyResponse.getMsg());

    }
}
