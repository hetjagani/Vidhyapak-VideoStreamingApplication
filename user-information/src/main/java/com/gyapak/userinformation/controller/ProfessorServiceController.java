package com.gyapak.userinformation.controller;

import com.gyapak.userinformation.model.Professor;
import com.gyapak.userinformation.model.ProfessorList;
import com.gyapak.userinformation.model.UserDetail;
import com.gyapak.userinformation.util.ProfessorRepository;
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
@RequestMapping(path = "/professor")
public class ProfessorServiceController {

    @Autowired
    private ProfessorRepository professorRepo;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping(path = "/all")
    public ResponseEntity<ProfessorList> getAllProfessors() {
        return ResponseEntity.ok(new ProfessorList(professorRepo.getAll()));
    }

    @GetMapping(path = "/{username}")
    public ResponseEntity<Professor> getProfessor(@PathVariable String username) {
        try {
            return ResponseEntity.ok(professorRepo.getProfessorFromUserName(username).get());
        }catch (NullPointerException nullEx) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username does not exist.");
        }

    }

    @PostMapping(path = "/save")
    public String saveProfessor(@RequestBody Professor p) {
        String role = "ROLE_PROFESSOR";
        String pass = p.getPersonPassword();
        p.setPersonPassword(BCrypt.hashpw(pass, BCrypt.gensalt()));

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        UserDetail ud = new UserDetail(p.getPersonUserName(), p.getPersonPassword(), true, authorities);

        if (professorRepo.save(p, ud) != 0) {
            return "Saved";
        } else {
            return "Not Saved";
        }
    }

    @PutMapping(path = "/update")
    public String updateProfessor(@RequestBody Professor p) {
        String role = "ROLE_PROFESSOR";
        String pass = p.getPersonPassword();

        if(pass != null && !pass.equals("")) {
            p.setPersonPassword(BCrypt.hashpw(pass, BCrypt.gensalt()));
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        UserDetail ud = new UserDetail(p.getPersonUserName(), p.getPersonPassword(), true, authorities);

        if (professorRepo.update(p, ud) != 0) {
            return "Updated";
        } else {
            return "Not Updated";
        }
    }

    @DeleteMapping(path = "/remove")
    public String deleteProfessor(String username, String id) {
        if(professorRepo.delete(id, username) != 0){
            return "Deleted";
        }else {
            return "Not Deleted";
        }
    }
}
