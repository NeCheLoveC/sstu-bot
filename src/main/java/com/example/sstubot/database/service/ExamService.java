package com.example.sstubot.database.service;

import com.example.sstubot.database.model.Exam;
import com.example.sstubot.database.repositories.ExamRepository;
import org.jsoup.helper.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

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
    public List<Exam> save(List<Exam> exams)
    {
        if(exams == null)
            throw new ValidationException("exams равны null");
        for(Exam exam : exams)
        {
            this.examRepository.save(exam);
        }
        return exams;
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
