package com.example.demo.Controller;


import com.example.demo.Model.User;
import com.example.demo.Repo.UserRepo;
import com.example.demo.Util.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class Controller {

    @Autowired
    UserRepo userRepo;
    @RequestMapping("/hello")
    public String get(){
        return "hello";
    }

    @GetMapping("/{userId}")
    public Optional<User> getUser(@PathVariable int userId){
       return userRepo.findById(userId);
    }
}
