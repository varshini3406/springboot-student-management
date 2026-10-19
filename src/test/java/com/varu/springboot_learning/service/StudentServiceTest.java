
package com.varu.springboot_learning.service;

import com.varu.springboot_learning.model.Course;
import com.varu.springboot_learning.model.Student;
import com.varu.springboot_learning.repository.CourseRepository;
import com.varu.springboot_learning.repository.StudentRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    void shouldReturnStudentWhenIdExists() {
        Course course = new Course("Java");
        Student student = new Student(1L, "Varu", course);

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(student));

        Student result = studentService.getStudentById(1L);

        assertSame(student, result);
    }
    @Test
void shouldThrowExceptionWhenStudentDoesNotExist() {

    when(studentRepository.findById(99L))
            .thenReturn(Optional.empty());

    assertThrows(
            RuntimeException.class,
            () -> studentService.getStudentById(99L)
    );
}

@Test
void shouldCallRepositoryWithCorrectId() {

    when(studentRepository.findById(1L))
            .thenReturn(Optional.empty());

    try {
        studentService.getStudentById(1L);
    } catch (RuntimeException exception) {
        // Expected if the student does not exist
    }

    verify(studentRepository).findById(1L);
}
}

