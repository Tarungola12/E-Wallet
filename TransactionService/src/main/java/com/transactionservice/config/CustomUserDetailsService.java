package com.transactionservice.config;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.commonservice.CommonConstants.CommonConstants;
import com.transactionservice.feign.UserFeign;

public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserFeign userFeign;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Going to make a call and the username is : "+username);
        String response = userFeign.validateUser(username);
        if (response==null){
            System.out.println("response is null");
            throw new UsernameNotFoundException("user not found");
        }
        System.out.println("Response: "+response);

        JSONObject jsonObject = new JSONObject(response);
        String password = jsonObject.optString(CommonConstants.USER_PASSWORD);
        String mobile = jsonObject.optString(CommonConstants.USER_MOBILE);

        return User.builder().username(mobile).password(password).build();
    }
}
