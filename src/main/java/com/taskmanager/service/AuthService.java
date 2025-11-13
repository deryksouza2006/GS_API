package com.taskmanager.service;

import com.taskmanager.model.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class AuthService {

    @Inject
    UserService userService;

    public Optional<User> authenticate(String username, String password) {
        return userService.authenticate(username, password);
    }
}