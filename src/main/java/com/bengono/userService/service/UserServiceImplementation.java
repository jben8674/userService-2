package com.bengono.userService.service;

import com.bengono.userService.configuration.JwtProvider;
import com.bengono.userService.model.User;
import com.bengono.userService.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@AllArgsConstructor
@Service
public class UserServiceImplementation implements UserService {
    @Autowired
    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();


    }
    @Override
    public User getUserProfile(String jwt) {
        String email = JwtProvider.getEmailFromJwtToken(jwt);
        return userRepository.findByEmail(email);
    }

}
