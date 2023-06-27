package com.contact.main.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.contact.main.dao.UserRepo;
import com.contact.main.entities.User;

public class UserDetailsServiceImple implements UserDetailsService{

	@Autowired
	private UserRepo UserRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
//		fetching user data from database
		User user = UserRepo.getUserByUserName(username);
		
		if (user==null) {
			throw new UsernameNotFoundException("Could not found user with this username!");
		}
		
		CustomUserDetails CustomeUserDetails=new CustomUserDetails(user);
		
		return CustomeUserDetails;
	}
	
	
}
