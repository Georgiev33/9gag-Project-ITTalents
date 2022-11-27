package com.ittalens.gag.services;

import com.ittalens.gag.model.dto.userdtos.*;
import com.ittalens.gag.model.entity.User;
import com.ittalens.gag.model.exceptions.BadRequestException;
import com.ittalens.gag.model.exceptions.NotFoundException;
import com.ittalens.gag.model.exceptions.UnauthorizedException;
import com.ittalens.gag.model.repository.CommentRepository;
import com.ittalens.gag.model.repository.PostRepository;
import com.ittalens.gag.model.repository.UserRepository;
import lombok.AllArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService{
    @Autowired
    private final UserRepository repository;
    @Autowired
    private final ModelMapper mapper;
    @Autowired
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private final EmailSenderServiceImpl emailSenderServiceImpl;
    @Autowired
    private final PostRepository postRepository;
    @Autowired
    private final CommentRepository commentRepository;

    @Override
    public void registerUser(RegisterUserDTO u) {
        u.setUserName(u.getUserName().trim());
        u.setPassword(u.getPassword().trim());
        u.setRepeatedPassword(u.getRepeatedPassword().trim());

        if (!validateEmail(u.getEmail())) {
            throw new BadRequestException("Email already exists!");
        }
        if (u.getAge() < 16 || u.getAge() > 119) {
            throw new BadRequestException("Invalid age!");
        }
        validatePassword(u.getPassword(), u.getRepeatedPassword());
        if (!isUserNameFree(u.getUserName()) && u.getUserName().length() < 3) {
            throw new BadRequestException("Invalid username!");
        }

        u.setRegisterDate(LocalDateTime.now());
        u.setActive(false);
        User user = mapper.map(u, User.class);
        String verificationCode = RandomString.make(50);
        user.setVerificationCode(verificationCode);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        repository.save(user);
        emailSenderServiceImpl.sendVerificationEmail(user);
    }

    @Override
    public UserWithoutPasswordDTO getUserById(long id) {
        User user = findById(id);
        return mapper.map(user, UserWithoutPasswordDTO.class);
    }

    @Override
    @Transactional
    public void delete(long id) {
        User u = findById(id);
        if (!u.isActive()) {
            throw new BadRequestException("User is already deleted.");
        }
        u.setUserName(RandomString.make(99));
        u.setEmail(RandomString.make(99));
        u.setFirstName(RandomString.make(99));
        u.setLastName(RandomString.make(99));
        u.setPassword(RandomString.make(99));
        u.setActive(false);
        repository.save(u);
        postRepository.deleteAllByCreatedBy(u.getId());
        commentRepository.deleteAllByCreatedBy(u.getId());
    }

    @Override
    public UserWithoutPasswordDTO edit(long userId, EditUserDTO editUserDTO) {
        User u = findById(userId);
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
            if (editUserDTO.getAge() < u.getAge() || editUserDTO.getAge() > 119) {
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

    @Override
    public UserWithoutPasswordDTO editPass(ChangePasswordDTO userDTO, long id) {
        User u = findById(id);
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

    @Override
    public UserWithoutPasswordDTO login(UserLoginDTO userDTO) {
        User user = repository.findUserByUserName(userDTO.getUsername()).orElseThrow(() -> new NotFoundException("User or password doesn't match."));
        if (!bCryptPasswordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new BadRequestException("User or password doesn't match.");
        }

        if (!user.isActive()) {
            throw new UnauthorizedException("User or password doesn't match.");
        }
        return mapper.map(user, UserWithoutPasswordDTO.class);
    }

    @Override
    public List<UserWithoutPasswordDTO> getAllUsers() {
        return repository.findAll().
                stream().map(user -> mapper.map(user, UserWithoutPasswordDTO.class)).
                collect(Collectors.toList());
    }

    @Override
    public UserWithoutPasswordDTO comparingVerificationCode(String code) {
        User user = repository.findByVerificationCode(code).orElseThrow(() -> new UnauthorizedException("Not correct verification code"));
        user.setActive(true);
        repository.save(user);
        return mapper.map(user, UserWithoutPasswordDTO.class);
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
        return repository.findExistingUsername(username) == null;
    }

    private void validatePassword(String password, String repeatedPassword) {
        if (!password.equals(repeatedPassword)) {
            throw new BadRequestException("Passwords don't match!");
        }
        if (password.length() < 8) {
            throw new BadRequestException("Password must be at least 8 symbols long.");
        }
    }

    private User findById(long uid) {
        return repository.findById(uid).orElseThrow(() -> new NotFoundException("User not found."));
    }
}
