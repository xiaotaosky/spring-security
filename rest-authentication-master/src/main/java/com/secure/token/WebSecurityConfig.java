package com.secure.token;

import com.secure.token.secure.MyUserDetailsService;
import com.secure.token.secure.TokenAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *  creates a Servlet Filter (springSecurityFilterChain ) responsible for all the security
 */
@EnableWebSecurity
//开启注释,允许使用@Secured, @PreAuthorize 和 @RolesAllowed 标签
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true,jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    //重新设置UserDetailsService
    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return userDetailsService;
    }

    @Bean
    public static PasswordEncoder passwordEncoder(){
        //return new BCryptPasswordEncoder();
        //BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(16);
        //String result = encoder.encode("123456");
        //System.out.println( result); //把该编码存储在数据库里作为密码，前端传密码时仍然为123456
        //System.out.print(encoder.matches("123456", "$2a$16$/kKskybcuMtB/QDspPbVbOfyiiCnQld4s2GQa7M8C3Zb.YBhdKTAO"));
       return  NoOpPasswordEncoder.getInstance();
    }


    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //http.addFilterBefore(tokenAuthenticationFilter,BasicAuthenticationFilter.class); //指定认证的过滤器
        http.addFilterAt(tokenAuthenticationFilter,UsernamePasswordAuthenticationFilter.class);
        http.authorizeRequests().antMatchers("/*").permitAll()
                .antMatchers("/secure/*").access("isAuthenticated()"); // secure路径下需要权限
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); //不创建session
        http.csrf().disable(); //disable csrf
    }
}