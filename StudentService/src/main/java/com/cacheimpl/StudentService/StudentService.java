package com.cacheimpl.StudentService;


import java.util.List;

public interface StudentService {
    List<Student> getAllStudents();
    Student createStudent(Student student);
}
