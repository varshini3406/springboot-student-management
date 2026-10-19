package com.varu.springboot_learning.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import com.varu.springboot_learning.model.Course;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Student name cannot be empty")
    private String name;

    @ManyToOne
@JoinColumn(name = "course_id", nullable = false)
private Course course;

    public Student() {
    }

    public Student(Long id, String name, Course course) {
        this.id = id;
        this.name = name;
        this.course = course;
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}

// /@NotNull	The value must not be null.
// @Positive	The number must be greater than zero.
// @NotBlank	The text must contain at least one non-whitespace character.
// For example, @NotBlank rejects an empty name like "" and a name containing only spaces like "   ".

// @Entity	Tells JPA this class represents a database entity.
// @Table(name = "students")	Uses the database table name students.
// @Id	Marks the primary key.
// @GeneratedValue	Lets the database generate the ID.
// @NotBlank	Keeps name and course from being empty when validation is applied