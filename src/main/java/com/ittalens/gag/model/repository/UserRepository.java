package com.ittalens.gag.model.repository;

import com.ittalens.gag.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findUsersByEmail(String email);

    List<User> findUsersByUserName(String username);

    Optional<User> findUserByUserName(String username);

    @Query(value = "SELECT * FROM users WHERE verification_code = ?", nativeQuery = true)
    Optional<User> findByVerificationCode(@Param("code") String code);
}
