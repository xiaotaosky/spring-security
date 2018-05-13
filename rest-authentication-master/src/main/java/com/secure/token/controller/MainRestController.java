package com.secure.token.controller;

import java.util.List;

import com.secure.token.secure.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainRestController.class);

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(value = "/test")
    public String test() {
        return "unauthenticated user can visit ";
    }

    @RequestMapping(value = "/secure/test")
    public String secureTest() {
        return "authenticated user can visit";
    }

    @RequestMapping(value = "/secure/login", method = RequestMethod.POST)
    public String login() {
        LOGGER.info(" *** MainRestController.login");
        return "login";
    }

    @RequestMapping(value = "/secure/logout", method = RequestMethod.POST)
    public String logout() {
        LOGGER.info(" *** MainRestController.logout");
        return "logout";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @RequestMapping("/secure/admin")
    public String admin() {
        LOGGER.info(" *** MainRestController.admin");
        return "Cool, you're admin!";
    }

    @RequestMapping("/secure/mytokens")
    public List<String> myTokens() {
        LOGGER.info(" *** MainRestController.myTokens");
        UserDetails currentUser = authenticationService.currentUser();
        return authenticationService.getUserTokens(currentUser.getUsername());
    }

    @PreAuthorize("hasRole('ADMIN') AND hasRole('SPECIAL')")
    @RequestMapping("/secure/special")
    public String special() {
        LOGGER.info(" *** MainRestController.special");
        return "ROLE_SPECIAL users should have access.";
    }

    @Secured("ROLE_USER")
    @RequestMapping("/secure/usertest")
    public UserDetails userTest() {
        LOGGER.info(" *** MainRestController.userTest");
        return authenticationService.currentUser();
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping("/secure/allusers")
    public List<String> allUsers() {
        LOGGER.info(" *** MainRestController.allUsers");
        return authenticationService.getValidUsers();
    }

}
