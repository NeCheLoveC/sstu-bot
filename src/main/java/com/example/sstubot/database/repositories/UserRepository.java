package com.example.sstubot.database.repositories;

import com.example.sstubot.database.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    @Query("select (count(u) > 0) from User u where lower(u.uniqueCode) like lower(:uniqueCode)")
    public boolean existsByUniqueCode(String uniqueCode);
    @Query("select u from User u where u.uniqueCode ilike :uniqueCode")
    public User getUserByUniqueCode(String uniqueCode);
    @Query("select u from User u where u.originalDocuments = true")
    public List<User> getUsersWithOriginal();
}
