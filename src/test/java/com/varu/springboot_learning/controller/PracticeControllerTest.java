
package com.varu.springboot_learning.controller;
import com.varu.springboot_learning.dto.StudentRequestDTO;

import com.varu.springboot_learning.model.Course;
import com.varu.springboot_learning.model.Student;
import com.varu.springboot_learning.service.StudentService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
@WithMockUser(username = "varu", roles = "USER")
class PracticeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    private Student createStudent(Long id, String name) {
        Course course = new Course();
        course.setId(1L);
        course.setName("Computer Science");

        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setCourse(course);
        return student;
    }

    @Test
    void shouldReturnStudentsJson() throws Exception {
        when(studentService.getAllStudents(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void shouldCreateStudentAndReturnJson() throws Exception {
        when(studentService.addStudentWithCourse(
                any(StudentRequestDTO.class)))
                .thenReturn(createStudent(1L, "Varu"));

        mockMvc.perform(post("/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Varu",
                                    "courseId": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Varu"))
                .andExpect(jsonPath("$.courseName")
                        .value("Computer Science"))
                .andExpect(jsonPath("$.courseId").value(1));
    }

    @Test
    void shouldRejectStudentWithEmptyName() throws Exception {
        mockMvc.perform(post("/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "",
                                    "courseId": 1
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnStudentById() throws Exception {
        when(studentService.getStudentById(1L))
                .thenReturn(createStudent(1L, "Varu"));

        mockMvc.perform(get("/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Varu"))
                .andExpect(jsonPath("$.courseName")
                        .value("Computer Science"));
    }

    @Test
    void shouldHandleStudentNotFound() throws Exception {
        when(studentService.getStudentById(999L))
                .thenThrow(new RuntimeException(
                        "Student not found with id: 999"));

        mockMvc.perform(get("/students/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteStudentSuccessfully() throws Exception {
        when(studentService.deleteStudent(1L)).thenReturn(true);

        mockMvc.perform(delete("/students/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(studentService).deleteStudent(1L);
    }

    @Test
void shouldUpdateStudentSuccessfully() throws Exception {
    when(studentService.updateStudent(
            org.mockito.ArgumentMatchers.eq(1L),
            any(StudentRequestDTO.class)))
            .thenReturn(createStudent(1L, "Unknown Student"));

    mockMvc.perform(put("/students/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                                "name": "Unknown Student",
                                "courseId": 1
                            }
                            """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Unknown Student"))
            .andExpect(jsonPath("$.courseName")
                    .value("Computer Science"));
}

@Test
void shouldReturn404WhenUpdatingMissingStudent() throws Exception {
    when(studentService.updateStudent(
            org.mockito.ArgumentMatchers.eq(999L),
            any(StudentRequestDTO.class)))
            .thenReturn(null);

    mockMvc.perform(put("/students/999")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                                "name": "Unknown Student",
                                "courseId": 1
                            }
                            """))
            .andExpect(status().isNotFound());
}


    @Test
    void shouldSearchStudentsByExactName() throws Exception {
        when(studentService.findStudentsByName("Varu"))
                .thenReturn(List.of(createStudent(1L, "Varu")));

        mockMvc.perform(get("/students/search/name")
                        .param("name", "Varu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Varu"))
                .andExpect(jsonPath("$[0].courseName")
                        .value("Computer Science"));
    }

    @Test
    void shouldSearchStudentsByCourse() throws Exception {
        when(studentService.findStudentsByCourse("Computer Science"))
                .thenReturn(List.of(createStudent(2L, "Ravi")));

        mockMvc.perform(get("/students/search/course")
                        .param("course", "Computer Science"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Ravi"))
                .andExpect(jsonPath("$[0].courseName")
                        .value("Computer Science"));
    }

    @Test
    void shouldSearchStudentsByPartialName() throws Exception {
        when(studentService.searchStudentsByName("Var"))
                .thenReturn(List.of(createStudent(3L, "Varshini")));

        mockMvc.perform(get("/students/search")
                        .param("name", "Var"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].name").value("Varshini"))
                .andExpect(jsonPath("$[0].courseName")
                        .value("Computer Science"));
    }

    @Test
    void shouldReturnEmptyListWhenNoStudentsMatchName()
            throws Exception {
        when(studentService.findStudentsByName("Nobody"))
                .thenReturn(List.of());

        mockMvc.perform(get("/students/search/name")
                        .param("name", "Nobody"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturnStudentsWithPagination() throws Exception {
        when(studentService.getAllStudents(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/students")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "name")
                        .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void shouldSupportDescendingSort() throws Exception {
        when(studentService.getAllStudents(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/students")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "name")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        verify(studentService).getAllStudents(
                argThat(pageable ->
                        pageable.getSort().getOrderFor("name") != null
                                && pageable.getSort()
                                .getOrderFor("name").isDescending()
                )
        );
    }

    @Test
    void shouldReturnBadRequestWhenSearchNameIsMissing()
            throws Exception {
        mockMvc.perform(get("/students/search/name"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenCourseIsMissing()
            throws Exception {
        mockMvc.perform(get("/students/search/course"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(studentService);
    }

    @Test
    void shouldReturnBadRequestForNonNumericPage() throws Exception {
        mockMvc.perform(get("/students")
                        .param("page", "abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(studentService);
    }

    @Test
    void shouldReturnBadRequestForNegativePage() throws Exception {
        mockMvc.perform(get("/students")
                        .param("page", "-1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(studentService);
    }
    @Test
    void shouldReturnBadRequestForZeroPageSize() throws Exception {
        mockMvc.perform(get("/students")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(studentService);
    }
}
