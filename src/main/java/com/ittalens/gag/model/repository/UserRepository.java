package com.ittalens.gag.model.repository;

import com.ittalens.gag.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findUsersByEmail(String email);

    @Query(value = "SELECT user_name FROM users WHERE user_name = ?1", nativeQuery = true)
    String findExistingUsername(String username);

    Optional<User> findUserByUserName(String username);

    @Query(value = "SELECT * FROM users WHERE verification_code = ?1", nativeQuery = true)
    Optional<User> findByVerificationCode(String code);
}
