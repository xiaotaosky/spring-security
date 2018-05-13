package com.secure.token.secure;

import java.io.IOException;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;

/**
 * 用于权限认证的拦截器
 */
@Service
public class TokenAuthenticationFilter extends GenericFilterBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(TokenAuthenticationFilter.class);
    private static final String HEADER_TOKEN = "X-Auth-Token";
    private static final String HEADER_USERNAME = "X-Username";
    private static final String HEADER_PASSWORD = "X-Password";
    private static final String AUTH_ERROR = "Auth_Error";
    private static final String FILTER_APPLIED_TAG = "filter already applied";

    private final String logoutLink = "/secure/logout";

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        LOGGER.info(" MyAuthenticationFilter.doFilter starts ");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        //处理Filter被多次执行的问题,GenericFilterBean会被自动添加到filterchain中，做安全认证时，会被加入到springSecurityChain中
        if(httpRequest.getAttribute(FILTER_APPLIED_TAG) != null){
            chain.doFilter(request, response);
            return;
        }
        httpRequest.setAttribute(FILTER_APPLIED_TAG,true);

        boolean stop = false;
        String token = httpRequest.getHeader(HEADER_TOKEN);  // 获取token
        if (token == null) {
            String userId = httpRequest.getHeader(HEADER_USERNAME);
            String password = httpRequest.getHeader(HEADER_PASSWORD);
            if (userId != null && password != null && httpRequest.getMethod().equals("POST")) {
                Map<String, Object> authResult = authenticationService.authenticate(userId, password); // 登录操作
                if (authResult != null && authResult.get("token") != null) {
                    httpResponse.setHeader(HEADER_TOKEN, authResult.get("token").toString());
                } else {
                    httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                }
                stop = true;
            }
        } else {
            if (authenticationService.checkToken(token)) { // 检查token的有效性
                LOGGER.info(HEADER_TOKEN + " valid for: " + token);
                if (httpRequest.getMethod().equals("POST") && currentLink(httpRequest).equals(logoutLink)) {  // 退出操作
                    authenticationService.logout(token);
                    stop = true;
                }
            } else {  // token无效
                LOGGER.info(" Invalid " + HEADER_TOKEN + ' ' + token);
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                stop = true;
            }
        }

        // 如果token有效或者token为空,且非登录或者退出操作，则通过拦截器进行下一步操作
        if (!stop) {
            chain.doFilter(request, response);
        } else {
            httpRequest.setAttribute(AUTH_ERROR, "");
        }
        LOGGER.info(" === AUTHENTICATION: " + SecurityContextHolder.getContext().getAuthentication());
    }

    private String currentLink(HttpServletRequest httpRequest) {
        if (httpRequest.getPathInfo() == null) {
            return httpRequest.getServletPath();
        }
        return httpRequest.getServletPath() + httpRequest.getPathInfo();
    }

}
