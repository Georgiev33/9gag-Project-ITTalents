package com.ittalens.gag.controller;

import com.ittalens.gag.model.dto.userdtos.*;

import com.ittalens.gag.model.exceptions.BadRequestException;
import com.ittalens.gag.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserServiceImpl userServiceImpl;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserDTO userDTO) {
        userServiceImpl.registerUser(userDTO);
        return ResponseEntity.ok("To complete your registration please verify your email.");
    }

    @PostMapping("/auth")
    public ResponseEntity<UserWithoutPasswordDTO> login(@RequestBody UserLoginDTO userDTO, HttpSession s) {
        if (s.getAttribute("LOGGED") != null) {
            throw new BadRequestException("You are already logged in!");
        }
        UserWithoutPasswordDTO result = userServiceImpl.login(userDTO);
        if (result != null) {
            s.setAttribute("LOGGED", true);
            s.setAttribute("USER_ID", result.getId());
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession s) {
        s.invalidate();
        return ResponseEntity.ok("Logged out.");
    }

    @PutMapping
    public ResponseEntity<UserWithoutPasswordDTO> edit(@RequestBody EditUserDTO userDTO, HttpSession s) {
        UserWithoutPasswordDTO result = userServiceImpl.edit(Long.parseLong(s.getAttribute("USER_ID").toString()), userDTO);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/pass")
    public ResponseEntity<UserWithoutPasswordDTO> editPass(@RequestBody ChangePasswordDTO userDTO, HttpSession s) {
        UserWithoutPasswordDTO result = userServiceImpl.editPass(userDTO, Long.parseLong(s.getAttribute("USER_ID").toString()));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserWithoutPasswordDTO> getById(@PathVariable long id) {
        return ResponseEntity.ok(userServiceImpl.getUserById(id));
    }

    @GetMapping
    public List<UserWithoutPasswordDTO> getAllUsers() {
        return userServiceImpl.getAllUsers();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(HttpSession session) {
        userServiceImpl.delete(Long.parseLong(session.getAttribute("USER_ID").toString()));
        return ResponseEntity.ok("Successfully deleted your account.");
    }

    @PutMapping("/verify/{code}")
    public ResponseEntity<UserWithoutPasswordDTO> verificatinCode(@PathVariable String code) {
        return ResponseEntity.ok(userServiceImpl.comparingVerificationCode(code));
    }
}
