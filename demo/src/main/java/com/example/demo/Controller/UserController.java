package com.example.demo.Controller;

import com.example.demo.Model.*;
import com.example.demo.Response.AuthResponse;
import com.example.demo.Service.AuthService;
import com.example.demo.Service.OtpService;
import com.example.demo.Service.UserService;
import com.example.demo.Util.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;


@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    @Autowired
    OtpService otpService;

    @PostMapping("/register/user")
    public ResponseEntity<Object> register(@RequestBody User user){
        if(user.getPassword().isEmpty() || user.getEmail().isEmpty()){
            return ResponseHandler.response(HttpStatus.BAD_REQUEST,true, "Invalid Object");
        }
        User isUser = userService.findByEmail(user.getEmail());
        System.out.println(isUser);
        if(Objects.nonNull(isUser) && isUser.isVerified()){
            return ResponseHandler.response(HttpStatus.ALREADY_REPORTED, true, "User Already Exit !!");
        }
        if(Objects.nonNull(isUser) && isUser.isRegister() && !isUser.isVerified()){
            AuthenticationToken authenticationToken = authService.generateToken(user);
            userService.sendVerificationMail(isUser,authenticationToken);
            return ResponseHandler.response(HttpStatus.OK, true, "Verification Link already sent to your email");
        }
        //u.setRegister(true);
         isUser= userService.register(user);
        AuthenticationToken authenticationToken = authService.generateToken(user);
        userService.sendVerificationMail(isUser,authenticationToken);
        return ResponseHandler.response(HttpStatus.OK, false, "Verification Link sent to your email", user.getEmail());
    }


    @PostMapping("/verify/register")
    public ResponseEntity<Object> verifyRegister(@RequestBody TokenForm tokenForm){
        if(tokenForm.getToken().isEmpty() || tokenForm.getEmail().isEmpty())
        {
            return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, "Invalid Objects");
        }
        User user = userService.findByEmail(tokenForm.getEmail());
        if(user.isVerified()){
            return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, "User Already Register");
        }
       AuthenticationToken authenticationToken = authService.findByTokenAndUser(tokenForm.getToken(),user);
        if(authenticationToken.isDeleted()){
            return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, "Token Expired");
        }

        if(authenticationToken.isExpired(authenticationToken)){
            user.setVerified(true);
            User savedUser = userService.saveUser(user);
            authenticationToken.setDeleted(true);
            AuthenticationToken authenticationToken1 = authService.deleted(authenticationToken);
            return ResponseHandler.response(HttpStatus.OK, false, "Verification Successful",user);
        }
        return ResponseHandler.response(HttpStatus.NOT_FOUND, true, "Invalid Link");
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginForm loginForm) {
        if(loginForm.getEmail().isEmpty() && loginForm.getPassword().isEmpty())
        {
            return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, "Invalid Objects");
        }
        User user = userService.loginUser(loginForm.getEmail());
        if(!Objects.nonNull(user) || !user.isVerified()){
            return ResponseHandler.response(HttpStatus.NOT_FOUND, true, "User is not Register");
        }
        if (user.isVerified()) {
            if (user.getPassword().equals(loginForm.getPassword())){
                Map map = new TreeMap();
                AuthenticationToken authenticationToken = authService.generateToken(user);
                map.put("User",user);
                map.put("Token",authenticationToken);
                return ResponseHandler.response(HttpStatus.OK, false, "Login Successful", map);}
        }
        return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, "User not found");
    }

    @PutMapping("/send/2FaOtp")
    public ResponseEntity<Object> send2Fa(@RequestParam String token){
        if(token.isEmpty()){
            return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, "Invalid Token");
        }
        AuthenticationToken authenticationToken = authService.findByToken(token);
        if(Objects.isNull(authenticationToken) || authenticationToken.isDeleted()){
            return ResponseHandler.response(HttpStatus.ALREADY_REPORTED, true, "Token Expired");
        }
        User user = authenticationToken.getUser();
        if(!Objects.nonNull(user) && !user.isVerified())
        {
            return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, "User not Register");
        }
        Map map = new TreeMap();
        int otp = otpService.sendOtp(user, "OTP for Login Verification --> ");
        String savedOtp = Integer.toString(otp);
        map.put("Token",authenticationToken.getToken());
        map.put("User",user);
        return ResponseHandler.response(HttpStatus.OK,true, "Otp sent succfully on E-mail",map);
    }

    @PutMapping("/verify/2FaOtp")
    public ResponseEntity<Object> verify2Fa(@RequestParam String token, @RequestParam int otp){
        if(token.isEmpty())
            return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, "Invalid Token");
        AuthenticationToken authenticationToken = authService.findByTokenAndIsDeleted(token,false);
        if(!Objects.nonNull(authenticationToken))
            return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, "Invalid Token");

        User user = authenticationToken.getUser();
        if(!user.isRegister() || !user.isVerified())
            return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, "Invalid User");

        Otp otp1 = otpService.findByOtpAndUser(otp,user);
        if(!Objects.nonNull(otp1))
        {
            return ResponseHandler.response(HttpStatus.NOT_FOUND,true, "Invalid Otp");
        }
        if(otp1.isDeleted()) {
            return ResponseHandler.response(HttpStatus.NOT_FOUND, true, "Otp Expired");
        }
           AuthenticationToken authToken = authService.login(user);
           Otp saveResponse = otpService.setIsDeleted(otp1);
           authenticationToken.setDeleted(true);
           authService.saveToken(authenticationToken);
            Map map = new TreeMap();
            map.put("Token",authToken.getToken());
            map.put("User",user);
            return ResponseHandler.response(HttpStatus.OK,false, "Otp Verify successfully",map);

//        return  ResponseHandler.response(HttpStatus.BAD_REQUEST, true, "error");
    }

    @DeleteMapping("/logout/user")
    public ResponseEntity<Object> logout(@RequestParam String token){
        if(token.isEmpty())
            return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, "Invalid Token");
        AuthenticationToken authenticationToken = authService.findByToken(token);
        if(Objects.isNull(authenticationToken))
            return ResponseHandler.response(HttpStatus.BAD_REQUEST,true, "Invalid Toekn");
        User user = authenticationToken.getUser();
        if(Objects.isNull(user))
            return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, "Invalid user");
        AuthResponse authResponse = authService.logout(token, user);
        if(authResponse.isStatus())
        {
            return ResponseHandler.response(HttpStatus.BAD_REQUEST,true, authResponse.getMessage().toString());
        }
        else
            return ResponseHandler.response(HttpStatus.OK, false, authResponse.getMessage().toString());
    }


    @PostMapping("/sendOtp/forget_password")
    public ResponseEntity<Object> sendCodeForgetPassword(@RequestBody ForgetPasswordForm forgetPasswordForm){
        if(forgetPasswordForm.getEmail().isEmpty())
            return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, "Enter Email");
        User user = userService.findByEmail(forgetPasswordForm.getEmail());
        if(Objects.isNull(user) || !user.isVerified())
            return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, "User not register");

        int otp = otpService.sendOtp(user,"OTP for ForgetPassword --> ");
        Map map = new TreeMap();
        map.put("Email",forgetPasswordForm.getEmail());
        AuthenticationToken authenticationToken = authService.generateToken(user);
        map.put("Token",authenticationToken.getToken());
        return ResponseHandler.response(HttpStatus.OK, false, "otp send on mail", map);
    }

    @PutMapping("/verifyOtp/forget_password")
    public ResponseEntity<Object> verifyOtpForgetPassword(@RequestParam String token, @RequestParam int otp)
    {
        if(token.isEmpty())
            return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, "Enter Token");
        AuthenticationToken authenticationToken = authService.findByTokenAndIsDeleted(token, false);
        if(Objects.isNull(authenticationToken))
            return ResponseHandler.response(HttpStatus.ALREADY_REPORTED, true, "Token Expired");
        User user = authenticationToken.getUser();
        if(Objects.isNull(user) || !user.isVerified())
            return ResponseHandler.response(HttpStatus.NOT_FOUND, true, "User doesn't exits");
        Otp otp1 = otpService.findByOtpAndUser(otp, user);
        if(Objects.isNull(otp1) || otp1.isDeleted())
            return ResponseHandler.response(HttpStatus.NOT_FOUND, true, "Invalid OTP");
        if(Objects.nonNull(otp1) && !otp1.isDeleted()){
            otp1.setDeleted(true);
            otpService.saveOtp(otp1);
            return ResponseHandler.response(HttpStatus.OK, false, "OTP verify success");
        }
        return ResponseHandler.response(HttpStatus.BAD_REQUEST,true, "Error");
    }

    @PutMapping("/reset/password")
    public ResponseEntity<Object> resetPassword(@RequestBody ForgetPasswordForm forgetPasswordForm, @RequestParam String token ){
        AuthenticationToken authenticationToken = authService.findByTokenAndIsDeleted(token, false);
        if(Objects.isNull(authenticationToken))
            return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, "Invalid Token");
        User user = authenticationToken.getUser();
        if(Objects.isNull(user) || !user.isVerified())
            return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, "User doesn't exit");
        if(forgetPasswordForm.getNewPassword().equals(forgetPasswordForm.getConfirmPassword())) {
            if(forgetPasswordForm.getEmail().equals(user.getEmail())) {
                user.setPassword(forgetPasswordForm.getNewPassword());
                user.setConfirmPassword(forgetPasswordForm.getConfirmPassword());
                User user1 = userService.saveUser(user);
                authenticationToken.setDeleted(true);
                authService.saveToken(authenticationToken);
                return ResponseHandler.response(HttpStatus.OK, true, "Password Reset successful");
            }else
                return ResponseHandler.response(HttpStatus.BAD_REQUEST, true, "Invalid Email");
        }
        return ResponseHandler.response(HttpStatus.NOT_FOUND, true, "Invalid Data");
    }

    @PostMapping("/saveUser")
    public ResponseEntity<Object> saveUser(@RequestBody User user){
        return ResponseHandler.response(HttpStatus.OK, false, "user save", userService.saveUser(user));
    }
}
