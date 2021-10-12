package com.example.demo.Service;

import com.example.demo.Model.AuthenticationToken;
import com.example.demo.Model.LoginDetails;
import com.example.demo.Model.Otp;
import com.example.demo.Model.User;
import com.example.demo.Repo.AuthRepo;
import com.example.demo.Repo.OtpRepo;
import com.example.demo.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class UserService {
    @Autowired
    UserRepo userRepo;

    @Autowired
    JavaMailSender mailSender;


    public User register(User u) {
        u.setRegister(true);
        return userRepo.save(u);
    }

    public void sendVerificationMail(User user, AuthenticationToken authenticationToken) {
    SimpleMailMessage simpleMailMessage =new SimpleMailMessage();
    simpleMailMessage.setFrom("vishupal251201@gmail.com");
    simpleMailMessage.setTo(user.getEmail());
    String url = "http://localhost:9595/verify/register/"+ authenticationToken.getToken();
    String message = "Verification Token --> " + authenticationToken.getToken();
    simpleMailMessage.setText(url);
    simpleMailMessage.setSubject(message);
    mailSender.send(simpleMailMessage);
    }

    public User findByEmail(String email) {
       return userRepo.findByEmail(email);
    }

    public User saveUser(User user) {
        return userRepo.save(user);
    }

    public User loginUser(String email) {
        return userRepo.findByEmail(email);
    }



}
