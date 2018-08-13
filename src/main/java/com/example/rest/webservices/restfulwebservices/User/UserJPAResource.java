package com.example.rest.webservices.restfulwebservices.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

//Don't forget to add getters,setters and default constructor to User class. It causes internal server errors when POSTing
@RestController
public class UserJPAResource {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    @GetMapping("/jpa/users")
    public List<User> retrieveAllUsers(){
        return userRepository.findAll();
    }

    @GetMapping("/jpa/users/{id}")
    public Resource<User> retrieveUser(@PathVariable int id){
        Optional<User> user= userRepository.findById(id);
        if (!user.isPresent()){
            throw new UserNotFoundException("id-"+id);
        }
        Resource<User> resource=new Resource<User>(user.get());
        ControllerLinkBuilder linkTo=linkTo(methodOn(this.getClass()).retrieveAllUsers());
        resource.add(linkTo.withRel("all-users"));
        return resource;
    }

    @DeleteMapping("/jpa/users/{id}")
    public void deleteUser(@PathVariable int id){
        userRepository.deleteById(id);
    }

    @PostMapping("/jpa/users")
    public  ResponseEntity<Object> createUser(@Valid @RequestBody User user){
        User savedUser =userRepository.save(user);

        URI location=ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();
        //URI with http://localhost:8080/users/{id}
        return ResponseEntity.created(location).build();
        //'201 Created' response is sent. Location=http://localhost:8080/users/4 is sent inside the response body.
    }

    @GetMapping("/jpa/users/{id}/posts")
    public List<Post> retrieveAllUsers(@PathVariable int id){
        Optional<User> userOptional=userRepository.findById(id);
        if (!userOptional.isPresent()){
            throw new UserNotFoundException("id-"+id);
        }
        return userOptional.get().getPosts();
    }

    @PostMapping("/jpa/users/{id}/posts")
    public  ResponseEntity<Object> createPost(@PathVariable int id, @RequestBody Post post) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            throw new UserNotFoundException("id-" + id);
        }
        User user = userOptional.get();
        post.setUser(user);
        postRepository.save(post);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(post.getId())
                .toUri();
        //URI with http://localhost:8080/users/{id}/posts/{id}ccc
        return ResponseEntity.created(location).build();
        //'201 Created' response is sent. Location=http://localhost:8080/users/{id}/posts/{id} is sent inside the response body.
    }
}
