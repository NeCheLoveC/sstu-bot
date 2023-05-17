package com.example.sstubot.database.service;

import com.example.sstubot.database.model.User;
import com.example.sstubot.database.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService
{
    protected UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User save(User user)
    {
        return userRepository.save(user);
    }

    public User getUserById(Long id)
    {
        return userRepository.findById(id).get();
    }

    public boolean userExist(String uniqueCode)
    {
        return userRepository.existsByUniqueCode(uniqueCode);
    }

    public User getUserByUniqueCode(String uniqueCode)
    {
        return userRepository.getUserByUniqueCode(uniqueCode);
    }
    public List<User> getUsersWithOriginal()
    {
        return userRepository.getUsersWithOriginal();
    }
}
