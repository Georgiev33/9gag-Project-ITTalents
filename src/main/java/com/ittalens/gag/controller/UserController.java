package com.ittalens.gag.controller;

import com.ittalens.gag.model.dto.userdtos.*;

import com.ittalens.gag.model.exceptions.BadRequestException;
import com.ittalens.gag.model.exceptions.UnauthorizedException;
import com.ittalens.gag.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;


    @PostMapping("/register")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<RegisterUserDTO> registerUser(@RequestBody RegisterUserDTO userDTO) {
        userService.registerUser(userDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth")
    public ResponseEntity<UserWithoutPasswordDTO> login(@RequestBody UserLoginDTO userDTO, HttpSession s) {
        UserWithoutPasswordDTO result = userService.login(userDTO);
        if (result != null) {
            s.setAttribute("LOGGED", true);
            s.setAttribute("USER_ID", result.getId());
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping()
    public ResponseEntity<UserWithoutPasswordDTO> edit(@RequestBody EditUserDTO userDTO, HttpSession s) {
        UserWithoutPasswordDTO result = userService.edit((Integer) s.getAttribute("USER_ID"), userDTO);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/pass")
    public ResponseEntity<UserWithoutPasswordDTO> editPass(@RequestBody ChangePasswordDTO userDTO, HttpSession s) {
        UserWithoutPasswordDTO result = userService.editPass(userDTO, (Integer) s.getAttribute("USER_ID"));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserWithoutPasswordDTO> getById(@PathVariable long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public List<UserWithoutPasswordDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{code}")
    public ResponseEntity<?> verificatinCode(@PathVariable String code){
        userService.comparingVerificationCode(code);
        return ResponseEntity.ok("Еmail has been verified");
    }
}
