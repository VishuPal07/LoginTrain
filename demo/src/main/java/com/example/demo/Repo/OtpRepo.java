package com.example.demo.Repo;

import com.example.demo.Model.Otp;
import com.example.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepo extends JpaRepository<Otp, Integer> {
    Otp findByOtp(int otp);

    void deleteByOtp(Otp otp1);

    Otp findByUser(User user);

    Otp findByOtpAndUser(int otp, User user);

}
