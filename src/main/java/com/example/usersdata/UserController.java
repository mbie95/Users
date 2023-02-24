package com.example.usersdata;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author marcin
 */
@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;
    private static final int DEFAULT_PAGE_SIZE = 5;

    public UserController(UserService service) {
        this.service = service;
    }
    
    @Transactional
    @PostMapping
    public ResponseEntity<String> postCSV(@RequestPart(value = "file") MultipartFile csvFile) {
        return new ResponseEntity<>(service.uploadCSV(csvFile), HttpStatus.OK);
    }
    
    @GetMapping(params = "quantity")
    ResponseEntity<Long> getUsersQuantity() {
        return new ResponseEntity<>(service.numberOfUsers(), HttpStatus.OK);
    }
    
    @GetMapping(params = "all")
    ResponseEntity<List<User>> getUsers(
            @RequestParam(value = "page", required = true) Integer pageNum, 
            @RequestParam(value = "size", required = false) Integer size) {
        if (size == null)
            size = DEFAULT_PAGE_SIZE;
        return new ResponseEntity<>(service.findAll(pageNum, size), HttpStatus.OK);
    }
    
    @GetMapping(params = "oldest")
    ResponseEntity<User> getOldestUser() {
        return new ResponseEntity<>(service.oldestUser(), HttpStatus.OK);
    }
    
    @GetMapping(path = "/{lastName}")
    ResponseEntity<Optional<User>> getUserByLastName(@PathVariable("lastName") String lastName) {
        return new ResponseEntity<>(service.findUserByLastName(lastName), HttpStatus.OK);
    }
    
    @Transactional
    @DeleteMapping(path = "/{phoneNumber}")
    public ResponseEntity<String> deleteUser(@PathVariable("phoneNumber") Integer phoneNumber) {
        return new ResponseEntity<>(service.deleteUser(phoneNumber), HttpStatus.OK);
    }
    
    @Transactional
    @DeleteMapping
    public ResponseEntity<String> deleteUsers() {
        return new ResponseEntity<>(service.deleteUsers(), HttpStatus.OK);
    }
}
