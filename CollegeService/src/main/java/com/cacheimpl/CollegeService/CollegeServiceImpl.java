package com.cacheimpl.CollegeService;


import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollegeServiceImpl implements CollegeService {

    private final CollegeRepository collegeRepository;

    public CollegeServiceImpl(CollegeRepository collegeRepository) {
        this.collegeRepository = collegeRepository;
    }

    @Override
    @Cacheable("colleges")
    public List<College> getAllColleges() {
        return collegeRepository.findAll();
    }

    @Override
    public College createCollege(College college) {
        return collegeRepository.save(college);
    }
}
