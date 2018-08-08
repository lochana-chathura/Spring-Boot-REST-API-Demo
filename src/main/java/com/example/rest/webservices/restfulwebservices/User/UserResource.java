package com.example.rest.webservices.restfulwebservices.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

//Don't forget to add getters,setters and default constructor to User class. It causes internal server errors when POSTing
@RestController
public class UserResource {
    @Autowired
    private UserDaoService service;

    @GetMapping("/users")
    public List<User> retrieveAllUsers(){
        return service.findAll();
    }

    @GetMapping("/users/{id}")
    public User retrieveUser(@PathVariable int id){
        return service.findOne(id);
    }

    @PostMapping("/users")
    public  ResponseEntity<Object> createdUser(@RequestBody User user){
        User savedUser =service.save(user);

        URI location=ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();
        //URI with http://localhost:8080/users/{id}
        return ResponseEntity.created(location).build();
        //'201 Created' response is sent. Location=http://localhost:8080/users/4 is sent inside the response body.
    }
}
