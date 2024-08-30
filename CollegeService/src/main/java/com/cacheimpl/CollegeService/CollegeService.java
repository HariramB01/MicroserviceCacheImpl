package com.cacheimpl.CollegeService;


import java.util.List;

public interface CollegeService {
    List<College> getAllColleges();
    College createCollege(College college);
}
