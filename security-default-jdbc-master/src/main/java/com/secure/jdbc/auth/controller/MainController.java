package com.secure.jdbc.auth.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class MainController {
    @RequestMapping("hello")
    public String hello(HttpServletRequest requset){
        System.out.print(requset.getRemoteUser());
        return "Hello World";
    }

    @RequestMapping("/api/user")
    public Object get(HttpServletRequest req) {
        SecurityContextImpl sci = (SecurityContextImpl) req.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
        if (sci != null) {
            Authentication authentication = sci.getAuthentication();
            if (authentication != null) {
                return authentication.getPrincipal();
            }
        }
        return "none";
    }

    @RequestMapping("/manager/operate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String managerOperate(HttpServletRequest req) {
        return "operation";
    }

}
