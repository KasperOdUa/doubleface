package com.example.doubleface.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EntryController {
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
