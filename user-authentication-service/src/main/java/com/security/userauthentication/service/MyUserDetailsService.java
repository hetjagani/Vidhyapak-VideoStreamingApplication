package com.security.userauthentication.service;

import com.security.userauthentication.model.UserDetail;
import com.security.userauthentication.util.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Get user from the database
        UserDetail ud = userRepo.getUserfromUsername(username);
        return new User(ud.getUsername(),ud.getPassword(), ud.getAuthorities());
    }
}
