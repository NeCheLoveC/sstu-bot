package com.example.sstubot.database.service;

import com.example.sstubot.database.model.Exam;
import com.example.sstubot.database.repositories.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ExamService
{
    protected ExamRepository examRepository;

    @Autowired
    public ExamService(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    public Exam save(Exam exam)
    {
        return examRepository.save(exam);
    }

    public boolean existExam(String name)
    {
        return examRepository.existsByName(name);
    }

    public Exam getExamByName(String name)
    {
        return examRepository.getExamByName(name);
    }
}
