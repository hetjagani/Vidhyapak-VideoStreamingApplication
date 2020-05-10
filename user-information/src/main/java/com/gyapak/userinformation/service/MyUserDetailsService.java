package com.gyapak.userinformation.service;

import com.gyapak.userinformation.model.UserDetail;
import com.gyapak.userinformation.util.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDetailsRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Get user from the database
        UserDetail ud = userRepo.getUserfromUsername(username);
        return new User(ud.getUsername(),ud.getPassword(), ud.getAuthorities());
    }
}
