package com.varu.springboot_learning.repository;

import com.varu.springboot_learning.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository
        extends JpaRepository<Course, Long> {
}