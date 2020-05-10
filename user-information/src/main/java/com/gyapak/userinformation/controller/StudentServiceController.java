package com.gyapak.userinformation.controller;

import com.gyapak.userinformation.model.Student;
import com.gyapak.userinformation.model.UserDetail;
import com.gyapak.userinformation.util.StudentRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/student")
public class StudentServiceController {

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping(path = "/all")
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentRepo.getAll());
    }

    @GetMapping(path = "/{username}")
    public ResponseEntity<Student> getStudent(@PathVariable String username) {
        try {
            return ResponseEntity.ok(studentRepo.getStudentFromUserName(username).get());
        } catch (NullPointerException nullEx) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username does not exist");
        }
    }

    @PostMapping(path = "/save")
    public String savePerson(@RequestBody Student s) {
        String role = "ROLE_STUDENT";
        String pass = s.getPersonPassword();
        s.setPersonPassword(BCrypt.hashpw(pass, BCrypt.gensalt()));

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        UserDetail ud = new UserDetail(s.getPersonUserName(), s.getPersonPassword(), true, authorities);

        if (studentRepo.save(s, ud) != 0) {
            return "Saved";
        } else {
            return "Not Saved";
        }
    }

    @PutMapping(path = "/update")
    public String updateStudent(@RequestBody Student s) {
        String role = "ROLE_STUDENT";
        String pass = s.getPersonPassword();

        if(pass != null && !pass.equals("")) {
            s.setPersonPassword(BCrypt.hashpw(pass, BCrypt.gensalt()));
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        UserDetail ud = new UserDetail(s.getPersonUserName(), s.getPersonPassword(), true, authorities);

        if (studentRepo.update(s, ud) != 0) {
            return "Updated";
        } else {
            return "Not Updated";
        }

    }
}
