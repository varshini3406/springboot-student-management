package com.varu.springboot_learning;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController 
public class PracticeController {
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to Spring Boot!";
    }

    @GetMapping("/greet")
    public String greet(@RequestParam String name) {
        return "Hello, " + name + "!";
    }

    @GetMapping("/square/{number}")
    public int square(@PathVariable int number) {
        return number * number;
    }
}


// @RestController  identifies the cls as a Rest controller, which means it can handle HTTP requests and return responses. It is a combination of @Controller and @ResponseBody annotations.
// @GetMapping("/welcome") maps GET requests to the /welcome URL to the welcome() method. When a GET request is made to /welcome, the welcome() method will be invoked, and its return value will be sent back as the response body.
// @GetMapping("/greet") maps GET requests to the /greet URL to the
// @RequestParam extracts a value from URL's query parameters. In this case, it extracts the value of the name parameter from the request URL and passes it to the greet() method.
// @PathVariable exttracts a value from the URL path. In this case, it extracts the value of the number parameter from the request URL and passes it to the square() method.


// http://localhost:8080/welcome --> returns "Welcome to Spring Boot!"
// http://localhost:8080/greet?name=Varu --> returns "Hello, Varu!"
// http://localhost:8080/square/5 --> returns 25

// /welcome is endpoint,welcome() is java method that handles request
//   /greet?name=Varu  
//  /greet is endpoint path
// ? begins query parameters, name=Varu is query parameter and varu is its value
// In /square/{number}, the curly braces mark a variable section of the path. When you visit /square/5, Spring Boot passes 5 to the method as an integer.