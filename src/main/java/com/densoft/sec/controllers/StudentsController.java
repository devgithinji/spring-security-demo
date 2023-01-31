package com.densoft.sec.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StudentsController {

    @GetMapping("/students")
    public String getStudents() {
        return "students";
    }
}
