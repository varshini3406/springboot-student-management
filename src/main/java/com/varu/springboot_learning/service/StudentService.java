
package com.varu.springboot_learning.service;

import com.varu.springboot_learning.model.Course;
import com.varu.springboot_learning.model.Student;
import com.varu.springboot_learning.repository.CourseRepository;
import com.varu.springboot_learning.repository.StudentRepository;

import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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
            .orElseThrow(() ->
                    new RuntimeException("Student not found with id: " + id));
}

    @Transactional 
    public Student addStudentWithCourse(String name, Long courseId) {
        Course course = getCourseById(courseId);

        Student student = new Student(null, name, course);
        Student savedStudent = studentRepository.save(student);

        logger.info("Added student: {}", savedStudent.getName());
        return savedStudent;
    }

    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Course not found"));
    }

    public Student updateStudent(Long id, Student updatedStudent) {
        Student existingStudent = studentRepository.findById(id)
                .orElse(null);

        if (existingStudent == null) {
            return null;
        }

        existingStudent.setName(updatedStudent.getName());

        if (updatedStudent.getCourse() != null
                && updatedStudent.getCourse().getId() != null) {
            Course course = getCourseById(
                    updatedStudent.getCourse().getId());
            existingStudent.setCourse(course);
        }

        return studentRepository.save(existingStudent);
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