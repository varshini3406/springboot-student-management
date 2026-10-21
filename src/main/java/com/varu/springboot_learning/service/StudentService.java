package com.varu.springboot_learning.service;

import com.varu.springboot_learning.dto.StudentRequestDTO;
import com.varu.springboot_learning.model.Course;
import com.varu.springboot_learning.model.Student;
import com.varu.springboot_learning.repository.CourseRepository;
import com.varu.springboot_learning.repository.StudentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class StudentService {

    private static final Logger logger =
            LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public StudentService(
            StudentRepository studentRepository,
            CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    public Page<Student> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Student not found"));
    }

    @Transactional
    public Student addStudentWithCourse(StudentRequestDTO request) {
        Course course = getCourseById(request.getCourseId());

        Student student = new Student(null, request.getName(), course);
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());

        Student savedStudent = studentRepository.save(student);

        logger.info("Added student: {}", savedStudent.getName());
        return savedStudent;
    }

    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Course not found"));
    }

    @Transactional
    public Student updateStudent(Long id, StudentRequestDTO request) {
        Student existingStudent = getStudentById(id);
        Course course = getCourseById(request.getCourseId());

        existingStudent.setName(request.getName());
        existingStudent.setEmail(request.getEmail());
        existingStudent.setPhone(request.getPhone());
        existingStudent.setCourse(course);

        Student savedStudent = studentRepository.save(existingStudent);

        logger.info("Updated student: {}", savedStudent.getName());
        return savedStudent;
    }

    public boolean deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            return false;
        }

        studentRepository.deleteById(id);
        return true;
    }

    public List<Student> findStudentsByName(String name) {
        return studentRepository.findByName(name);
    }

    public List<Student> findStudentsByCourse(String courseName) {
        return studentRepository.findByCourse_NameIgnoreCase(courseName);
    }

    public List<Student> searchStudentsByName(String name) {
        return studentRepository.findByNameContainingIgnoreCase(name);
    }
}
// - @Service tells Spring that this class provides application logic.
// - List<Student> holds multiple student objects in memory.
// - getAllStudents() returns all students.
// - getStudentById() finds a student by ID.
// - addStudent() adds a student to the list.
// This list is temporary storage. Its contents disappear when the application restarts.