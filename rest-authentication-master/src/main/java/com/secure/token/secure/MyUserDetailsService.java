package com.secure.token.secure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service("MyUserDetailsService")
public class MyUserDetailsService implements UserDetailsService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MyUserDetailsService.class);

	@Autowired
	private AuthenticationService authenticationService;
	
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException {
		LOGGER.info(" MyUseDetailService.loadUserByUsername");
		UserDetails user = authenticationService.getUserByUsername(userName);
		if(user==null){
			throw new UsernameNotFoundException("User " + userName + " not found");
		}
		LOGGER.info(" user: " + user );
		return user;
	}

}
