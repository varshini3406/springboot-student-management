package com.varu.springboot_learning.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
@OneToMany(mappedBy = "course", cascade = CascadeType.PERSIST)
private List<Student> students = new ArrayList<>();
    public Course() {
    }

    public Course(String name) {
        this.name = name;
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

    public List<Student> getStudents() {
    return students;
}

public void setStudents(List<Student> students) {
    this.students = students;
}
}