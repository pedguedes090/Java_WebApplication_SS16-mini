package org.example.session16.service;

import org.example.session16.model.dto.LoginDTO;
import org.example.session16.model.dto.RegisterDTO;
import org.example.session16.model.entity.User;

import java.util.Optional;

public interface UserService {
    User register(RegisterDTO registerDTO);
    Optional<User> login(LoginDTO loginDTO);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
}

