package com.lucas.springbootpostgres.controller;

import com.lucas.springbootpostgres.exception.ResourceNotFoundException;
import com.lucas.springbootpostgres.model.Product;
import com.lucas.springbootpostgres.model.User;
import com.lucas.springbootpostgres.repositories.ProductRepository;
import com.lucas.springbootpostgres.repositories.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
@RestController
@RequestMapping("api/v1")
public class MyController {
    @Autowired SessionFactory sessionFactory;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    //Show all users
    @GetMapping("/showUsers")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    //Insert user
    @PostMapping("/insertUser")
    public User createUser(@RequestBody User user) {
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12));
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    @PostMapping("/selectUser")
    public ResponseEntity<Map<String, Boolean>> getUser(@RequestBody User user) throws Exception {
        Map<String, Boolean> response = new HashMap<>();
        try {
            Session session = sessionFactory.openSession();

            Query password_query = session.createQuery("SELECT password FROM User WHERE mail = :mail");
            password_query.setParameter("mail", user.getMail());
            String stored_psw = (String) password_query.getSingleResult();

            if (BCrypt.checkpw(user.getPassword(), stored_psw)) {
                System.out.println("Passwords are matching");
                response.put("Found:", Boolean.TRUE);
                return ResponseEntity.ok(response);
            }

        }
        catch (Exception e) {
            response.put("Found:", Boolean.FALSE);
        }
        return ResponseEntity.ok(response);
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



    @GetMapping("showProducts")
    public List<Product> showProducts(){
        return productRepository.findAll();
    }

    @PostMapping("/insertProduct")
    public Product createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }
}
