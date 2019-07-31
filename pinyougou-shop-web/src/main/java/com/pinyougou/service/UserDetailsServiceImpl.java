package com.pinyougou.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;

/**
 * @typeName: UserDetailsServiceImpl
 * @classDesc: 认证类
 * @author：XiaoBai
 * @createTime：2019年7月24日 下午2:05:00
 * @remarks： 
 * @version V1.0
 */
public class UserDetailsServiceImpl implements UserDetailsService {
	
	private SellerService sellerService;
	

	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}



	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
		
		TbSeller seller = sellerService.findOne(username);
		if(seller != null && seller.getStatus().equals("1")) {
			return new User(username, seller.getPassword(), authorities);			
		} else return null;
		
	}


}
