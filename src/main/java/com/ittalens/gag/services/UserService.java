package com.ittalens.gag.services;

import com.ittalens.gag.model.dto.RegisterUserDTO;
import com.ittalens.gag.model.dto.UserWithoutPasswordDTO;
import com.ittalens.gag.model.entity.User;
import com.ittalens.gag.model.exceptions.BadRequestException;
import com.ittalens.gag.model.exceptions.NotFoundException;
import com.ittalens.gag.model.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private ModelMapper mapper;
    public void registerUser(RegisterUserDTO u){
        if(!validEmail(u.getEmail())){
            throw new BadRequestException("email already exists.");
        }
        if(u.getAge() < 16 || u.getAge() > 110){
            throw new BadRequestException("Invalid age.");
        }
        if(!u.getPassword().equals(u.getRepeatedPassword())){
            throw new BadRequestException("Passwords don't match!");
        }
        if(repository.findUsersByUserName(u.getUserName()).size() > 0){
            throw new BadRequestException("Username already exists");
        }
        u.setRegisterDate(LocalDateTime.now());
        u.setActive(true);
        User user = mapper.map(u, User.class);
        repository.save(user);
    }
    public UserWithoutPasswordDTO getUserById(long id) {
        Optional<User> optionalUser = repository.findById(id);
        if(optionalUser.isPresent()){
            return mapper.map(optionalUser.get(), UserWithoutPasswordDTO.class);
        }
        throw new NotFoundException("User doesn't exist.");
    }

    public void delete(long id) {
        if(repository.findById(id).isPresent()) {
            User u = repository.findById(id).get();
            if(!u.isActive()){
                throw new BadRequestException("User is already deleted.");
            }
            u.setActive(false);
            repository.save(u);
            return;
        }
        throw new NotFoundException("User doesn't exist.");
    }

    public List<UserWithoutPasswordDTO> getAllUsers() {
        List<UserWithoutPasswordDTO> users =
                repository.
                findAll().
                stream().map(user -> mapper.map(user, UserWithoutPasswordDTO.class)).collect(Collectors.toList());
        return users;
    }

    private boolean validEmail(String email){
        if(!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")){
            throw new BadRequestException("Invalid email.");
        }
        if(repository.findUsersByEmail(email).size() > 0){
            throw new BadRequestException("Email already exists.");
        }
        return true;
    }
}
