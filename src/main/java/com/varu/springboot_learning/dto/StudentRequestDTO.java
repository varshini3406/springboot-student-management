package com.varu.springboot_learning.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StudentRequestDTO {

    @NotBlank(message = "Student name cannot be empty")
    private String name;

    @NotNull(message = "Course ID cannot be empty")
    private Long courseId;

    public StudentRequestDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}