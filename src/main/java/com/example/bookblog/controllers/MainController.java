package com.example.bookblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private static final String TITLE_ATTRIBUTE = "title";

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute(TITLE_ATTRIBUTE, "Главная страница");
        return "home";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute(TITLE_ATTRIBUTE, "Добавить статью");
        return "add";
    }

    @GetMapping("/news")
    public String news(Model model) {
        model.addAttribute(TITLE_ATTRIBUTE, "Новинки");
        return "news";
    }

    @GetMapping("/top")
    public String top(Model model) {
        model.addAttribute(TITLE_ATTRIBUTE, "Рейтинг");
        return "top";
    }
}
