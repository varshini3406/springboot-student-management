package com.varu.springboot_learning.dto;

public class StudentResponseDTO {

    private Long id;
    private String name;
    private String course;

    public StudentResponseDTO(
            Long id, String name, String course) {
        this.id = id;
        this.name = name;
        this.course = course;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCourse() {
        return course;
    }
}