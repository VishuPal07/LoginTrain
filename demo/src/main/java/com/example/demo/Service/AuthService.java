package com.example.demo.Service;

import com.example.demo.Model.AuthenticationToken;
import com.example.demo.Model.LoginDetails;
import com.example.demo.Model.User;
import com.example.demo.Repo.AuthRepo;
import com.example.demo.Repo.LoginDetailRepo;
import com.example.demo.Response.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    AuthRepo authRepo;

    @Autowired
    UserService userService;

    @Autowired
    LoginDetailRepo loginDetailRepo;

    public AuthenticationToken generateToken(User user){
        String token = UUID.randomUUID().toString();
        AuthenticationToken authenticationToken = new AuthenticationToken(user, token);
        authRepo.saveAndFlush(authenticationToken);
        return authenticationToken;
    }

    public AuthenticationToken deleted(AuthenticationToken authenticationToken) {
        return authRepo.save(authenticationToken);
    }

    public AuthenticationToken findByToken(String token) {
        return authRepo.findByToken(token);
    }

    public AuthResponse logout(String token, User user) {
        AuthResponse authResponse = new AuthResponse();
        List<LoginDetails> loginDetails = loginDetailRepo.findByUserAndIsDeleted(user,false);
        if(loginDetails.isEmpty()){
            authResponse.setStatus(false);
            authResponse.setMessage("User not login");
            return authResponse;
        }
        for(LoginDetails details : loginDetails) {
            details.setDeleted(true);
            loginDetailRepo.save(details);
        }
        AuthenticationToken authenticationToken = authRepo.findByToken(token);
        authenticationToken.setDeleted(true);
        authRepo.save(authenticationToken);
        authResponse.setMessage("Logout successful");
        authResponse.setStatus(false);
        return authResponse;
    }

    public AuthenticationToken findByTokenAndIsDeleted(String token, boolean b) {
        return authRepo.findByTokenAndIsDeleted(token,b);
    }

    public AuthenticationToken login(User user) {
        LoginDetails loginDetails = new LoginDetails();
        loginDetails.setDeleted(false);
        loginDetails.setUser(user);
        loginDetailRepo.save(loginDetails);
        return generateToken(user);
    }

    public AuthenticationToken findByTokenAndUser(String token,User user) {
        return authRepo.findByTokenAndUser(token,user);
    }

    public void saveToken(AuthenticationToken authenticationToken) {
         authRepo.save(authenticationToken);
    }
}


