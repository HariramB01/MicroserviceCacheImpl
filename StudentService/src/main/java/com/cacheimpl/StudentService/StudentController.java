package com.cacheimpl.StudentService;


import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}")
    public Optional<Student> getAllStudentById(@PathVariable Long id) {
        return studentService.getAllStudentById(id);
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @PutMapping("/update/{id}")
    public Student updateStudent(@PathVariable Long id ,@RequestBody Student student) {
        return studentService.updateStudent(id, student);
    }

}
