package com.varu.springboot_learning;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest(properties = "spring.flyway.enabled=false")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticatedUserShouldBeRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/students"))
                .andExpect(status().isUnauthorized());
    }

    @Test
@WithMockUser(username = "varu", roles = "USER")
void regularUserShouldBeDeniedAccessToCourses() throws Exception {
    mockMvc.perform(get("/courses"))
            .andExpect(status().isForbidden());
}


@Test
@WithMockUser(username = "varu", roles = "USER")
void regularUserShouldAccessStudents() throws Exception {
    mockMvc.perform(get("/students"))
            .andExpect(status().isOk());
}

@Test
@WithMockUser(username = "admin", roles = "ADMIN")
void adminShouldAccessCourses() throws Exception {
    mockMvc.perform(get("/courses"))
            .andExpect(status().isOk());
}


@Test
@WithAnonymousUser
void unauthenticatedUserShouldBeDeniedAccessToCourses() throws Exception {
    mockMvc.perform(get("/courses"))
            .andExpect(status().isUnauthorized());
}



@Test
@WithMockUser(username = "varu", roles = "USER")
void regularUserShouldBeDeniedWhenCreatingCourse() throws Exception {
    mockMvc.perform(post("/courses")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                        "name": "Spring Boot Testing"
                    }
                    """))
            .andExpect(status().isForbidden());
}

@Test
@WithMockUser(username = "admin", roles = "ADMIN")
void adminShouldBeAllowedWhenCreatingCourse() throws Exception {
    mockMvc.perform(post("/courses")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                    {
                        "name": "Spring Boot Testing"
                    }
                    """))
            .andExpect(status().isCreated());
}



@Test
@WithMockUser(username = "varu", roles = "USER")
void userShouldNotAccessStudentsInCourse() throws Exception {
    mockMvc.perform(get("/courses/1/students"))
            .andExpect(status().isForbidden());
}

@Test
@WithMockUser(username = "admin", roles = "ADMIN")
void adminShouldAccessStudentsInCourse() throws Exception {
    mockMvc.perform(get("/courses/1/students"))
            .andExpect(status().isOk());
}

}
