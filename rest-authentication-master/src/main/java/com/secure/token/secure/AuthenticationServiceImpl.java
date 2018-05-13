package com.secure.token.secure;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

import com.secure.token.dao.TokenManagerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenManagerMapper tokenManagerMapper;

    /**
     * 登录认证
     *
     * @param userId
     * @param password
     * @return
     */
    @Override
    public Map<String, Object> authenticate(String userId, String password) {
        LOGGER.info(" AuthenticationServiceImpl.authenticate");
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(userId, password);
            authentication = authenticationManager.authenticate(authentication); //登录认证，最终调用UserDetailService.loadUserByUsername
            SecurityContextHolder.getContext().setAuthentication(authentication);
            if (authentication.getPrincipal() != null) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                String newToken = createNewToken(userDetails);
                if (!StringUtils.isEmpty(newToken)) {
                    map.put("token", newToken);
                    map.put("userDetails", userDetails);
                }
            }
        } catch (AuthenticationException e) {
            LOGGER.error(" AuthenticationServiceImpl.authenticate - FAILED: " + e.toString());
        }
        return map;
    }

    /**
     * 检查token的有效性
     *
     * @param token
     * @return
     */
    @Override
    public boolean checkToken(String token) {
        LOGGER.info(" AuthenticationServiceImpl.checkToken token : " + token);
        UserDetails userDetails = getUserDetails(token);
        if (userDetails == null) {
            return false;
        }

        Authentication securityToken = new PreAuthenticatedAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(securityToken);

        return true;
    }

    /**
     * 退出登录
     *
     * @param token
     */
    @Override
    public void logout(String token) {
        removeToken(token);
        LOGGER.info(" AuthenticationServiceImpl.logout token: " + token);
        SecurityContextHolder.clearContext();
    }

    /**
     * 通过用户名获取token
     *
     * @param username
     * @return
     */
    @Override
    public List<String> getUserTokens(String username) {
        return tokenManagerMapper.getUserTokens(username);
    }

    /**
     * 获取当前有效登录用户
     *
     * @return
     */
    @Override
    public List<String> getValidUsers() {
        return tokenManagerMapper.getValidUser();
    }

    /**
     * 通过token获取用户详情
     *
     * @param token
     * @return
     */
    @Override
    public UserDetails getUserDetails(String token) {
        List<Map<String, Object>> results = tokenManagerMapper.getUserByToken(token);
        return getUserFromQueryResult(results);
    }

    /**
     * 通过用户名获取用户详情
     *
     * @param username
     * @return
     */
    @Override
    public MyUserDetails getUserByUsername(String username) {
        List<Map<String, Object>> results = tokenManagerMapper.getUserByUsername(username);
        return getUserFromQueryResult(results);
    }

    /**
     * 获取当前用户
     *
     * @return
     */
    @Override
    public UserDetails currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return (UserDetails) authentication.getPrincipal();
    }

    /**
     * 生成token
     *
     * @param user
     * @return
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String createNewToken(UserDetails user) {
        // 生成token
        String token;
        while (true) {
            token = generateToken();
            int count = tokenManagerMapper.findTokenCount(token);
            if (count == 0) {
                break;
            }
        }

        //失效该用户的历史token
        tokenManagerMapper.invalidUserToken(user.getUsername());

        Map<String, String> map = new HashMap<String, String>();
        map.put("token", token);
        map.put("username", user.getUsername());
        int insertCount = tokenManagerMapper.addToken(map);
        if (insertCount == 0) {
            LOGGER.info("insert token fail");
            return null;
        }

        return token;
    }

    private String generateToken() {
        byte[] tokenBytes = new byte[32];
        new SecureRandom().nextBytes(tokenBytes);
        return new String(Base64.encode(tokenBytes), StandardCharsets.UTF_8);
    }

    private void removeToken(String token) {
        tokenManagerMapper.invalidUserTokenByToken(token);
    }

    private MyUserDetails getUserFromQueryResult(List<Map<String, Object>> results) {
        MyUserDetails user = null;
        if (!CollectionUtils.isEmpty(results)) {
            String name = null;
            String password = null;
            Set<String> authorities = new HashSet<>();
            for (Map<String, Object> result : results) {
                name = (String) result.get("username");
                password = (String) result.get("password");
                authorities.add((String) result.get("authority"));
            }
            user = new MyUserDetails(name, password, authorities);
        }
        return user;
    }

}

