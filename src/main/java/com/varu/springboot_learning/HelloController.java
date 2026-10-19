package com.varu.springboot_learning;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello Varu! My first Spring Boot API works!";
    }
}

// @RestController is a convenience annotation that combines @Controller and @ResponseBody, eliminating the need to annotate every request handling method of the controller class with the @ResponseBody annotation.
// @RestController --> tells spring boot that this class handles web requests and returns responses.

// @GetMapping("/hello")--> maps GET requests to the /hello URL to this method. When a GET request is made to /hello, the sayHello() method will be invoked, and its return value will be sent back as the response body.
// sayHello() method executes when the matching req arrives.
// return sends the message back as the HTTP response.