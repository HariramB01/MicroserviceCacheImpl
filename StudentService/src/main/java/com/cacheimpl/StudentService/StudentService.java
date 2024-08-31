package com.cacheimpl.StudentService;


import java.util.List;
import java.util.Optional;

public interface StudentService {
    List<Student> getAllStudents();
    Student createStudent(Student student);

    Optional<Student> getAllStudentById(Long id);

    Student updateStudent(Long id, Student student);
}
