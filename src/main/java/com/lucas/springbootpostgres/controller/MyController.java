package com.lucas.springbootpostgres.controller;

import com.lucas.springbootpostgres.exception.ResourceNotFoundException;
import com.lucas.springbootpostgres.model.Order;
import com.lucas.springbootpostgres.model.Product;
import com.lucas.springbootpostgres.model.User;
import com.lucas.springbootpostgres.repositories.OrderRepository;
import com.lucas.springbootpostgres.repositories.ProductRepository;
import com.lucas.springbootpostgres.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    //Show all users
    @Transactional
    @GetMapping("/showUsers")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    //Insert user
    @Transactional
    @PostMapping("/insertUser")
    public ResponseEntity<String> createUser(@RequestBody User user) throws Exception {
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12));
        user.setPassword(hashedPassword);
        List<User> users = userRepository.findAll();
        for (User u: users) {
            if (u.getMail().equals((user.getMail()))) {
                String msg = "E-mail already in use";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
            }
        }
        userRepository.save(user);
        String msg = "User created";
        return ResponseEntity.ok(msg);
    }

    @Transactional
    @PostMapping("/selectUser")
    public ResponseEntity<Map<String, Boolean>> getUser(@RequestBody User user) throws Exception {
        Map<String, Boolean> response = new HashMap<>();
        Session session = sessionFactory.openSession();

        try {

            Query password_query = session.createQuery("SELECT password FROM User WHERE mail = :mail");
            password_query.setParameter("mail", user.getMail());
            String stored_psw = (String) password_query.getSingleResult();

            if (BCrypt.checkpw(user.getPassword(), stored_psw)) {
                System.out.println("Passwords are matching");
                response.put("Found:", Boolean.TRUE);
                session.close();
                return ResponseEntity.ok(response);
            }
            else {
                System.out.println("Passwords are not matching");
                response.put("Found:", Boolean.FALSE);
                session.close();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        }
        catch (Exception e) {
            response.put("Found:", Boolean.FALSE);
            session.close();
        }
        session.close();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @Transactional
    @PostMapping("/getIdByMail")
    public Long getUserId(@RequestBody User user) {
        try {
            String mail = user.getMail();
            System.out.println("mail is " + mail);
            Session session = sessionFactory.openSession();
            Query q = session.createQuery("SELECT id FROM User WHERE mail = :mail");
            q.setParameter("mail", mail);
            Long id = (Long)q.getSingleResult();
            session.close();
            return id;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    //Update user
    @Transactional
    @PutMapping("/updateUsers/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " doesn't exist"));
        user.setMail(userDetails.getMail());
        user.setName(userDetails.getName());
        String hashedPassword = BCrypt.hashpw(userDetails.getPassword(), BCrypt.gensalt(12));
        user.setPassword(hashedPassword);
        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    //Delete employy
    @Transactional
    @DeleteMapping("removeUsers/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " doesn't exist"));
        userRepository.delete(user);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }


    @Transactional
    @GetMapping("/showProducts")
    public List<Product> showProducts(){
        return productRepository.findAll();
    }

    @Transactional
    @PostMapping("/insertProduct")
    public Product createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @Transactional
    @GetMapping("/showOrders")
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    @GetMapping("/showOrders/{user_id}")
    public ResponseEntity<List<Order>> getOrdersOfUser(@PathVariable Long user_id) {
        Session session = sessionFactory.openSession();
        Query q = session.createQuery("FROM Order where user_id = :user_id");
        q.setParameter("user_id", user_id);

        List<Order> orders = q.getResultList();
        session.close();
        return ResponseEntity.ok(orders);
    }

    @Transactional
    @PostMapping("/insertOrder")
    public ResponseEntity<Map<String, Boolean>> insertOrder (@RequestBody Order order) {
        Map<String, Boolean> response = new HashMap<>();
        try {
            orderRepository.save(order);
            response.put("inserted", Boolean.TRUE);
        }
        catch (Exception e){
            response.put("inserted", Boolean.FALSE);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }
    @Transactional
    @DeleteMapping("/deleteOrder/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteOrder (@PathVariable Long id) {
        Order order = orderRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Order with id: " + id + " doesn't exist"));
        orderRepository.delete(order);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return ResponseEntity.ok(response);
    }
}
