package com.cacheimpl.StudentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    private static final String CACHE_KEY_PREFIX = "student:";
    private static final String CACHE_KEY_ALL = CACHE_KEY_PREFIX + "all";

    @Override
    public List<Student> getAllStudents() {
        // Fetch the list of students from Redis cache
        List<Student> students = (List<Student>) redisTemplate.opsForValue().get(CACHE_KEY_ALL);

        // Check if cache miss
        if (students == null) {
            System.out.println("Fetching from DB...");
            // Fetch from the database
            students = studentRepository.findAll();
            if (!students.isEmpty()) {
                // Store in Redis with TTL only if not already present
                redisTemplate.opsForValue().setIfAbsent(CACHE_KEY_ALL, students, 30, TimeUnit.SECONDS);
            }
        } else {
            System.out.println("Fetching from Redis...");
        }

        return students;
    }

    @Override
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Optional<Student> getAllStudentById(Long id) {
        // Check cache first
        Student cachedStudent = (Student) redisTemplate.opsForValue().get(CACHE_KEY_PREFIX + id);
        if (cachedStudent != null) {
            System.out.println("Cache Hit on Student: " + id);
            return Optional.of(cachedStudent);
        }

        // Load from the database if cache miss
        Optional<Student> studentFromDB = studentRepository.findById(id);
        if (studentFromDB.isPresent()) {
            Student student = studentFromDB.get(); // Cache miss
            // Store in Redis with TTL only if not already present
            redisTemplate.opsForValue().setIfAbsent(CACHE_KEY_PREFIX + id, student, 10, TimeUnit.SECONDS);
            System.out.println("Data from DB and added to cache");
            return Optional.of(student);
        }

        // Return Optional.empty() if the student is not found




        return Optional.empty();
    }

    @Override
    public Student updateStudent(Long id, Student student) {
        Student existingStudentData = studentRepository.findById(id).get();

        existingStudentData.setEmail(student.getEmail());
        existingStudentData.setName(student.getName());
        Student redisStudentData = (Student) redisTemplate.opsForValue().get(CACHE_KEY_PREFIX+id);
        System.out.println("Student data from redis cache: "+redisStudentData);
        System.out.print("Updated the new data: ");
        redisTemplate.opsForValue().set(CACHE_KEY_PREFIX+id, existingStudentData);
        System.out.println(existingStudentData);
        studentRepository.save(existingStudentData);
        return existingStudentData;
    }


}
