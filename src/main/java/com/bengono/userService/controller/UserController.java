package com.bengono.userService.controller;


import com.bengono.userService.model.User;
import com.bengono.userService.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService useService;


    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader("Authorization") String jwt) {
        List<User> users = useService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) {
        User user = useService.getUserProfile(jwt);
        return new ResponseEntity<>(user, HttpStatus.OK);

    }

}