package com.cacheimpl.CollegeService;


import java.util.List;
import java.util.Optional;

public interface CollegeService {
    List<College> getAllColleges();
    College createCollege(College college);

    Optional<College> getCollegeById(Long id);

    College updateCollege(Long id, College college);
}
