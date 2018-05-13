package com.secure.token.secure;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * 处理token服务
 */
public interface AuthenticationService {
    Map<String, Object> authenticate(String userId, String password);  //认证

    boolean checkToken(String token); //检查token有效性

    void logout(String token); //退出清空token

    UserDetails currentUser(); //当前用户

    List<String> getUserTokens(String username); //获取用户有效token

    List<String> getValidUsers(); // 获取有效登录用户

    UserDetails getUserDetails(String token); //获取用户详情

    UserDetails getUserByUsername(String username); //获取用户详情
}

