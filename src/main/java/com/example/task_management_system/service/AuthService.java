package com.example.task_management_system.service;

import com.example.task_management_system.model.User;
import com.example.task_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public boolean login(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get().getPassword().equals(password);
        }
        // For dummy purpose, if user doesn't exist, create one with the password
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setCreatedBy("SYSTEM");
        newUser.setModifiedBy("SYSTEM");
        userRepository.save(newUser);
        return true;
    }
}
