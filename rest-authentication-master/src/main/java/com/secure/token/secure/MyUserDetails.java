package com.secure.token.secure;

import java.util.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

/**
 * 重定义UserDetails，用于表示用户
 */
public class MyUserDetails implements UserDetails {
    private static final long serialVersionUID = -757241665662741727L;

    private String userName;
    private String password;
    private Set<String> auths;

    public MyUserDetails(String userName, String password,
                         Set<String> auths) {
        this.userName = userName;
        this.password = password;
        this.auths = auths;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        for (String authority : auths) {
            if (!StringUtils.isEmpty(authority))
                authorities.add(new SimpleGrantedAuthority(authority));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o != null && o instanceof MyUserDetails
                && Objects.equals(userName, ((MyUserDetails) o).userName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userName);
    }

    @Override
    public String toString() {
        return "UserContext{" + "user=" + userName + '}';
    }
}
