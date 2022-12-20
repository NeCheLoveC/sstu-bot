package com.example.sstubot.database.repositories;

import com.example.sstubot.database.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
