package com.security.userauthentication;

import com.security.userauthentication.model.AuthenticationRequest;
import com.security.userauthentication.model.AuthenticationResponse;
import com.security.userauthentication.service.MyUserDetailsService;
import com.security.userauthentication.util.JwtUtil;
import com.security.userauthentication.util.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/authentication")
public class Resource {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private MyUserDetailsService userDetails;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepo;

    @PostMapping(path = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest req) throws Exception {

        //First authenticate the user
        Authentication auth = null;
        try {
            auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );
        }catch (BadCredentialsException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Once the user is authenticated we fetch userdetails to generate JWT TOKEN
        SecurityContextHolder.getContext().setAuthentication(auth);
        final String token = jwtUtil.generateToken(auth);

        return ResponseEntity.ok(new AuthenticationResponse(token));
    }
}
