package edu.itmo.isticketservice.controllers;

import edu.itmo.isticketservice.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/example")
@RequiredArgsConstructor
public class ExampleController {

    private final UserService userService;

    @GetMapping("/test")
    public String example() {
        return "Hello World!";
    }

    @GetMapping("/auth-test")
    public String authTest() {
        return "You are authenticated!";
    }

    @GetMapping("/admin-test")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String admin() {
        return "Hello, admin!";
    }

}
