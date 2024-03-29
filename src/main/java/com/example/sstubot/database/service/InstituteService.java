package com.example.sstubot.database.service;

import com.example.sstubot.database.model.Institute;
import com.example.sstubot.database.repositories.InstituteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class InstituteService
{
    protected InstituteRepository instituteRepository;

    @Autowired
    public InstituteService(InstituteRepository instituteRepository) {
        this.instituteRepository = instituteRepository;
    }
    public Institute save(Institute institute)
    {
        return instituteRepository.save(institute);
    }
    public Institute getInstituteByName(String name)
    {
        return instituteRepository.getInstituteByName(name);
    }
    public Long countInstance()
    {
        return this.instituteRepository.countAllInstitute();
    }
}
