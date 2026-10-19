package com.varu.springboot_learning;
// a java record is a way to represent data.this record defines the three fields our API expects
public record Student(Long id, String name, String course) {
    
}
