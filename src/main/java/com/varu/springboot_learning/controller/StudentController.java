
package com.varu.springboot_learning.controller;

import com.varu.springboot_learning.dto.StudentRequestDTO;
import com.varu.springboot_learning.dto.StudentResponseDTO;
import com.varu.springboot_learning.model.Student;
import com.varu.springboot_learning.service.StudentService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // Search students by exact name
    @GetMapping("/search/name")
    public List<StudentResponseDTO> searchByName(
            @RequestParam String name) {

        return studentService.findStudentsByName(name)
                .stream()
                .map(student -> new StudentResponseDTO(
                        student.getId(),
                        student.getName(),
                        student.getCourse().getName()
                ))
                .toList();
    }

    // Search students by course name
    @GetMapping("/search/course")
    public List<StudentResponseDTO> searchByCourse(
            @RequestParam String course) {

        return studentService.findStudentsByCourse(course)
                .stream()
                .map(student -> new StudentResponseDTO(
                        student.getId(),
                        student.getName(),
                        student.getCourse().getName()
                ))
                .toList();
    }

    // Search students by partial name
    @GetMapping("/search")
    public List<StudentResponseDTO> searchByPartialName(
            @RequestParam String name) {

        return studentService.searchStudentsByName(name)
                .stream()
                .map(student -> new StudentResponseDTO(
                        student.getId(),
                        student.getName(),
                        student.getCourse().getName()
                ))
                .toList();
    }

    // Get students with pagination and sorting
    @GetMapping
    public Page<StudentResponseDTO> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return studentService.getAllStudents(pageable)
                .map(student -> new StudentResponseDTO(
                        student.getId(),
                        student.getName(),
                        student.getCourse() != null
                                ? student.getCourse().getName()
                                : null
                ));
    }

    // Get one student by ID
    @GetMapping("/{id}")
    public StudentResponseDTO getStudentById(
            @PathVariable Long id) {

        Student student = studentService.getStudentById(id);

        if (student == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Student not found");
        }

        return new StudentResponseDTO(
                student.getId(),
                student.getName(),
                student.getCourse() != null
                        ? student.getCourse().getName()
                        : null
        );
    }

    // Add a student using an existing course ID
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponseDTO addStudent(
            @Valid @RequestBody StudentRequestDTO request) {

        Student student = studentService.addStudentWithCourse(
                request.getName(),
                request.getCourseId()
        );

        return new StudentResponseDTO(
                student.getId(),
                student.getName(),
                student.getCourse().getName()
        );
    }

    // Update a student
    @PutMapping("/{id}")
    public StudentResponseDTO updateStudent(
            @PathVariable Long id,
            @RequestBody Student updatedStudent) {

        Student student =
                studentService.updateStudent(id, updatedStudent);

        if (student == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Student not found");
        }

        return new StudentResponseDTO(
                student.getId(),
                student.getName(),
                student.getCourse() != null
                        ? student.getCourse().getName()
                        : null
        );
    }

    // Delete a student
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable Long id) {

        boolean deleted = studentService.deleteStudent(id);

        if (!deleted) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Student not found");
        }
    }
}


// / @PostMapping("/students") handles POST requests sent to /students
// @RequestBody Student student tells Spring Boot to deserialize the incoming JSON request body into a Student object. The JSON data sent in the request body is automatically converted into a Student instance based on the fields defined in the Student record.
// return student;  returns that student as JSON in the response
// @ResponseStatus(HttpStatus.CREATED) sets the HTTP status code of the response to 201 Created, indicating that a new resource has been successfully created.

// For now, this endpoint only returns the submitted student. It doesn't save the student permanently or store it in a list. We'll add storage in a late
//  browser is easy for get req but for post req i am using postman to send post req with json data in body.

// Spring automatically supplies the StudentService object. This is called dependency injection.
// The flow
// When a request arrives, the controller calls the service. The service handles the operation and returns the data to the controller.

// @PutMapping("/{id}")	Handles PUT requests for a specific student
// @DeleteMapping("/{id}")	Handles DELETE requests for a specific student
// @ResponseStatus(HttpStatus.NO_CONTENT)	Returns status 204 when deletion succeeds
// ResponseStatusException	Allows us to return an HTTP error such as 404
// Notice that the controller checks whether the student exists. If not, it returns 404 Not Found instead of pretending the operation succeeded.

// @RequestBody converts the incoming JSON into a Student object.



// @Valid checks the validation rules on that object.



// If a rule fails, Spring rejects the request instead of calling the service method.
