
package com.varu.springboot_learning.repository;

import com.varu.springboot_learning.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByName(String name);

    List<Student> findByCourse_NameIgnoreCase(String courseName);

    List<Student> findByNameContainingIgnoreCase(String name);
}

// JpaRepository<Student, Long> tells Spring Data JPA:
// - Student is the entity we want to manage.
// - Long is the type of its ID.
// - The repository provides built-in operations such as save(), findAll(), findById() and deleteById().
// You don't need to write SQL for these basic operations!