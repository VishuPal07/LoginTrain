package com.example.demo.Controller;

import com.example.demo.Model.AuthenticationToken;
import com.example.demo.Model.User;
import com.example.demo.Repo.AuthRepo;
import com.example.demo.Util.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthenticationController {
    @Autowired
    AuthRepo authRepo;

    @GetMapping("/authToken")
    public AuthenticationToken findByToken(@RequestParam String token){
        return authRepo.findByToken(token);
    }

    @PostMapping("/saveAuhtToken")
    public ResponseEntity<Object> save(@RequestBody AuthenticationToken authenticationToken){
        authenticationToken.setDeleted(false);
        return ResponseHandler.response(HttpStatus.OK, false, "auht save", authRepo.save(authenticationToken));
    }
}
