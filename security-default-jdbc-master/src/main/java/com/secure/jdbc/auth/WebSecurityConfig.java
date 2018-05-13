package com.secure.jdbc.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;

/**
 * creates a Servlet Filter (springSecurityFilterChain ) responsible for all the security
 */
@Configuration
@EnableWebSecurity
//开启注释,允许使用@Secured, @PreAuthorize 和 @RolesAllowed 标签
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private DataSource dataSource;

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        manager.createUser(User.withUsername("user").password("123456").roles("ADMIN").build());
//        return manager;
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager();
        manager.setDataSource(dataSource);
        manager.setEnableGroups(true);  //开启用户--分组--权限模式，调试模式可以确定manager的SQL语句，进而建表或定制化自己的表
        manager.setEnableAuthorities(false); //关闭用户--权限模式
        return manager;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        //String result = encoder.encode("myPassword");
        //return new BCryptPasswordEncoder(16);
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic();
        http.formLogin()
                .loginPage("/login").defaultSuccessUrl("/api/user").permitAll()
                .and().logout().permitAll()
                .and().authorizeRequests()
                .antMatchers("/resources/**", "/csrftoken", "/auth/login").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/db/**").access("hasRole('ADMIN') and hasRole('DBA')")
                .anyRequest().authenticated();

    }
}