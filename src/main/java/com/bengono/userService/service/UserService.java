package com.bengono.userService.service;

import com.bengono.userService.model.User;

import java.util.List;

public interface UserService {


    public List<User> getAllUsers();
    public User getUserProfile(String jwt);
}
