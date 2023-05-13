package com.example.sstubot.database.repositories;

import com.example.sstubot.database.model.Institute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InstituteRepository extends JpaRepository<Institute, Long>
{
    public Institute getInstituteByName(String name);
    @Query("select count(i.id) from Institute i")
    public Long countAllInstitute();
}
