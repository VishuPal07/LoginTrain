package com.example.demo.Repo;

import com.example.demo.Model.LoginDetails;
import com.example.demo.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginDetailRepo extends JpaRepository<LoginDetails, Integer> {
    List<LoginDetails> findByUserAndIsDeleted(User user, boolean b);
}
