package com.secure.token.dao;

import java.util.List;
import java.util.Map;

public interface TokenManagerMapper {
    int findTokenCount(String token);  // 查找token个数

    int addToken(Map<String, String> map);  //新增token

    int invalidUserToken(String username);  //失效token

    int invalidUserTokenByToken(String token);  //失效token

    List<Map<String, Object>> getUserByUsername(String username); //登录时，查询用户信息

    List<Map<String, Object>> getUserByToken(String token);  //ͨ通过token，获取用户信息

    List<String> getUserTokens(String username);  //获取用户有效token

    List<String> getValidUser();  //获取当前全部有效用户

}
