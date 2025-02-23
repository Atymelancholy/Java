package com.example.bookblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BlogController {

    @GetMapping("/forum")
    public String forum(Model model) {
        return "forum.html";
    }

    @GetMapping("/sing")
    public String sing(Model model) {
        return "sing.html";
    }
}
