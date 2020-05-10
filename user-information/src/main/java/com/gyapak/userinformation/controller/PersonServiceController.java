package com.gyapak.userinformation.controller;

import com.gyapak.userinformation.model.Person;
import com.gyapak.userinformation.util.PersonRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/person")
public class PersonServiceController {

    @Autowired
    private PersonRepository personRepository;

    @GetMapping(path = "/all")
    public ResponseEntity<List<Person>> getAllPersons() {
        return ResponseEntity.ok(personRepository.getAll());
    }

    @GetMapping(path = "/{username}")
    public ResponseEntity<Person> getAllPersons(@PathVariable String username) {
        try {
            return ResponseEntity.ok(personRepository.getPersonFromUserName(username).get());
        }catch (NullPointerException nullEx) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username does not exist");
        }
    }

    @GetMapping(path = "/usernameExist")
    public ResponseEntity<Boolean> userNameExist(String username) {
        try {
            Optional<Person> optPer = personRepository.getPersonFromUserName(username);
            if(!optPer.isEmpty())
                return ResponseEntity.ok(true);
        }catch (NullPointerException nullEx) {
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(false);
    }
}
