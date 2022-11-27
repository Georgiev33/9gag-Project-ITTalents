package com.ittalens.gag.services;

import com.ittalens.gag.model.dto.userdtos.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IUserService {


    void registerUser(RegisterUserDTO u);

    UserWithoutPasswordDTO getUserById(long id);

    @Transactional
    void delete(long id);

    UserWithoutPasswordDTO edit(long userId, EditUserDTO editUserDTO);

    UserWithoutPasswordDTO editPass(ChangePasswordDTO userDTO, long id);

    UserWithoutPasswordDTO login(UserLoginDTO userDTO);

    List<UserWithoutPasswordDTO> getAllUsers();

    UserWithoutPasswordDTO comparingVerificationCode(String code);
}
