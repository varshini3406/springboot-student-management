package com.varu.springboot_learning.dto;

public class StudentResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private Long courseId;
    private String courseName;

    public StudentResponseDTO() {
    }

    public StudentResponseDTO(
            Long id,
            String name,
            String email,
            String phone,
            Long courseId,
            String courseName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.courseId = courseId;
        this.courseName = courseName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}