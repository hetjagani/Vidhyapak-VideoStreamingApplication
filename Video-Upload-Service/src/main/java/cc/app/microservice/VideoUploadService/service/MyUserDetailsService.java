package cc.app.microservice.VideoUploadService.service;

import cc.app.microservice.VideoUploadService.data.UserDetailsRepository;
import cc.app.microservice.VideoUploadService.model.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
