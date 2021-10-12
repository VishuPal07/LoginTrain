package com.example.demo.Controller;

import com.example.demo.Model.Otp;
import com.example.demo.Model.User;
import com.example.demo.Repo.OtpRepo;
import com.example.demo.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class OtpController {
    @Autowired
    OtpRepo otpRepo;
    @Autowired
    UserRepo userRepo;

    @GetMapping("/saveOtp")
    public Otp saveotp(@RequestParam Otp otp){
        return otpRepo.save(otp);
    }

    @GetMapping("/getUserOtp")
    public Otp getOtpAndUser(@RequestParam String otp ,@RequestParam int user){
        User user1 =userRepo.getById(user);
        return otpRepo.findByOtpAndUser(Integer.parseInt(otp), user1);
    }
}
