package com.ittalens.gag.controller;

import com.ittalens.gag.model.dto.ErrorDTO;
import com.ittalens.gag.model.dto.UserDTO;
import com.ittalens.gag.model.entity.User;

import com.ittalens.gag.model.exceptions.BadRequestException;
import com.ittalens.gag.model.exceptions.NotFoundException;
import com.ittalens.gag.model.repository.UserRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository repository;
    @Autowired
    private ModelMapper mapper;

    @PostMapping("/register")
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserDTO register(@RequestBody User u){
            if(repository.findUsersByEmail(u.getEmail()).size() > 0){
                throw new BadRequestException("email already exists.");
            }
            if(u.getAge() < 16 || u.getAge() > 110){
                throw new BadRequestException("Invalid age.");
            }
            if(repository.findUsersByUserName(u.getUserName()).size() > 0){
                throw new BadRequestException("Username already exists");
            }
            u.setRegisterDate(LocalDateTime.now());
            u.setActive(true);
            repository.save(u);
            return mapper.map(u, UserDTO.class);
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable long id){
        Optional<User> optionalUser = repository.findById(id);
        if(optionalUser.isPresent()){
            return mapper.map(optionalUser.get(), UserDTO.class);
        }
        throw new NotFoundException("User doesn't exist.");
    }

}
