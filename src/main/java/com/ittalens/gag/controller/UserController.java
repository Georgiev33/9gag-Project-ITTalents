package com.ittalens.gag.controller;

import com.ittalens.gag.model.dto.RegisterUserDTO;
import com.ittalens.gag.model.dto.UserWithoutPasswordDTO;

import com.ittalens.gag.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<RegisterUserDTO> registerUser(@RequestBody RegisterUserDTO userDTO){
            userService.registerUser(userDTO);
            return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserWithoutPasswordDTO> getById(@PathVariable long id){
       return ResponseEntity.ok(userService.getUserById(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id){
       userService.delete(id);
       return ResponseEntity.ok().build();
    }
    @GetMapping
    public List<UserWithoutPasswordDTO> getAllUsers(){
       return userService.getAllUsers();
    }
}
