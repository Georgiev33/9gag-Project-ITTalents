package com.ittalens.gag.model.repository;

import com.ittalens.gag.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findUsersByEmail(String email);
    List<User> findUsersByUserName(String username);
}
