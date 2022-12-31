package com.example.sstubot.database.repositories;

import com.example.sstubot.database.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long>
{
    @Query("select (count(e) > 0) from Exam e where e.name LIKE :name")
    public boolean existsByName(String name);

    @Query("select e from Exam e where e.name LIKE :name")
    public Exam getExamByName(String name);
}
