package com.example.demo.Repo;

import com.example.demo.Model.AuthenticationToken;
import com.example.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepo extends JpaRepository<AuthenticationToken, Integer> {
    AuthenticationToken findByToken(String token);

    AuthenticationToken findByTokenAndIsDeleted(String token, boolean b);

    AuthenticationToken findByTokenAndUser(String token, User user);

    AuthenticationToken findByUser(User user);
}
