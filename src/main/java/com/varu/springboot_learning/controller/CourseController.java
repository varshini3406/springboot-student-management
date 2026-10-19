

package com.varu.springboot_learning.controller;

import com.varu.springboot_learning.dto.StudentResponseDTO;
import com.varu.springboot_learning.model.Course;
import com.varu.springboot_learning.repository.CourseRepository;
import com.varu.springboot_learning.service.StudentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseRepository courseRepository;
    private final StudentService studentService;

    public CourseController(
            CourseRepository courseRepository,
            StudentService studentService) {
        this.courseRepository = courseRepository;
        this.studentService = studentService;
    }

    @GetMapping
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @PreAuthorize ("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Course addCourse(@RequestBody Course course) {
        course.setId(null);
        return courseRepository.save(course);
    }

    @GetMapping("/{courseId}/students")
    public List<StudentResponseDTO> getStudentsByCourse(
            @PathVariable Long courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Course not found"));

        return course.getStudents().stream()
                .map(student -> new StudentResponseDTO(
                        student.getId(),
                        student.getName(),
                        course.getName()))
                .toList();
    }
}
