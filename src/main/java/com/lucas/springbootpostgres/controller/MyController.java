package com.lucas.springbootpostgres.controller;

import com.lucas.springbootpostgres.exception.ResourceNotFoundException;
import com.lucas.springbootpostgres.model.User;
import com.lucas.springbootpostgres.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("api/v1")
public class MyController {

    @Autowired
    private UserRepository userRepository;

    //Show all users
    @GetMapping("/showUsers")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    //Insert user
    @PostMapping("/insertUser")
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    //Get user by id
    @GetMapping("/showUsers/{id}")
    public ResponseEntity<User> getUserByid(@PathVariable Long id) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " doesn't exist"));
            return ResponseEntity.ok(user);
    }


    //Update user

    @PutMapping("updateUsers/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " doesn't exist"));
        user.setMail(userDetails.getMail());
        user.setName(userDetails.getName());

        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    //Delete employy
    @DeleteMapping("removeUsers/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " doesn't exist"));
        userRepository.delete(user);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }
}
