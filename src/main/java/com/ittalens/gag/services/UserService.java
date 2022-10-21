package com.ittalens.gag.services;

import com.ittalens.gag.model.dto.userdtos.*;
import com.ittalens.gag.model.entity.User;
import com.ittalens.gag.model.exceptions.BadRequestException;
import com.ittalens.gag.model.exceptions.NotFoundException;
import com.ittalens.gag.model.exceptions.UnauthorizedException;
import com.ittalens.gag.model.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void registerUser(RegisterUserDTO u) {
        if (!validateEmail(u.getEmail())) {
            throw new BadRequestException("email already exists.");
        }
        if (u.getAge() < 16 || u.getAge() > 110) {
            throw new BadRequestException("Invalid age.");
        }
        if (!u.getPassword().equals(u.getRepeatedPassword())) {
            throw new BadRequestException("Passwords don't match!");
        }
        if (repository.findUsersByUserName(u.getUserName()).size() > 0) {
            throw new BadRequestException("Username already exists");
        }
        u.setRegisterDate(LocalDateTime.now());
        u.setActive(true);
        User user = mapper.map(u, User.class);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        repository.save(user);
    }

    public UserWithoutPasswordDTO getUserById(long id) {
        Optional<User> optionalUser = repository.findById(id);
        if (optionalUser.isPresent()) {
            return mapper.map(optionalUser.get(), UserWithoutPasswordDTO.class);
        }
        throw new NotFoundException("User doesn't exist.");
    }

    public void delete(long id) {
        if (repository.findById(id).isPresent()) {
            User u = repository.findById(id).get();
            if (!u.isActive()) {
                throw new BadRequestException("User is already deleted.");
            }
            u.setActive(false);
            repository.save(u);
            return;
        }
        throw new NotFoundException("User doesn't exist.");
    }

    public UserWithoutPasswordDTO edit(long userId, EditUserDTO editUserDTO) {
        Optional<User> user = repository.findById(userId);
        if (user.isPresent()) {
            User u = user.get();
            if (editUserDTO.getUserName() != null && !editUserDTO.getUserName().equals(u.getUserName())) {
                if (!isUserNameFree(editUserDTO.getUserName())) {
                    throw new BadRequestException("Unique usernames only!");
                }
                u.setUserName(editUserDTO.getUserName());
            }
            if (editUserDTO.getFirstName() != null && !editUserDTO.getFirstName().equals(u.getFirstName())) {
                u.setFirstName(editUserDTO.getFirstName());
            }
            if (editUserDTO.getLastName() != null && !editUserDTO.getLastName().equals(u.getLastName())) {
                u.setLastName(editUserDTO.getLastName());
            }
            if (editUserDTO.getAge() != u.getAge() && editUserDTO.getAge() != 0) {
                if (editUserDTO.getAge() < u.getAge() || editUserDTO.getAge() > 110) {
                    throw new BadRequestException("Invalid age edit.");
                }
                u.setAge(editUserDTO.getAge());
            }
            if (editUserDTO.getEmail() != null && !editUserDTO.getEmail().equals(u.getEmail())) {
                if (!validateEmail(editUserDTO.getEmail())) {
                    throw new BadRequestException("Invalid email edit.");
                }
                u.setEmail(editUserDTO.getEmail());
            }
            repository.save(u);
            return mapper.map(u, UserWithoutPasswordDTO.class);
        }
        throw new NotFoundException("No such user.");
    }

    public UserWithoutPasswordDTO editPass(ChangePasswordDTO userDTO, long id) {
        Optional<User> optionalUser = repository.findById(id);
        if (optionalUser.isPresent()) {
            User u = optionalUser.get();
            if (!bCryptPasswordEncoder.matches(userDTO.getCurrentPassword(), u.getPassword())) {
                throw new BadRequestException("Invalid credentials.");
            }
            if (!userDTO.getNewPassword().equals(userDTO.getConfirmNewPassword())) {
                throw new BadRequestException("Invalid credentials.");
            }
            u.setPassword(bCryptPasswordEncoder.encode(userDTO.getNewPassword()));
            repository.save(u);
            return mapper.map(u, UserWithoutPasswordDTO.class);
        }
        throw new BadRequestException("No such user.");
    }

    public UserWithoutPasswordDTO login(UserLoginDTO userDTO) {
        User user = repository.findUserByUserName(userDTO.getUsername()).orElseThrow(() -> new NotFoundException("User or password doesn't match."));
        if (!bCryptPasswordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new BadRequestException("User or password doesn't match.");
        }

        if (!user.isActive()){
            throw new UnauthorizedException("User or password doesn't match.");
        }
        return mapper.map(user, UserWithoutPasswordDTO.class);
    }

    public List<UserWithoutPasswordDTO> getAllUsers() {
        List<UserWithoutPasswordDTO> users =
                repository.
                        findAll().
                        stream().map(user -> mapper.map(user, UserWithoutPasswordDTO.class)).collect(Collectors.toList());
        return users;
    }

    private boolean validateEmail(String email) {
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new BadRequestException("Invalid email.");
        }
        if (repository.findUsersByEmail(email).size() > 0) {
            throw new BadRequestException("Email already exists.");
        }
        return true;
    }

    private boolean isUserNameFree(String username) {
        if (!repository.findUserByUserName(username).isPresent()) {
            return true;
        }
        return false;
    }
}
