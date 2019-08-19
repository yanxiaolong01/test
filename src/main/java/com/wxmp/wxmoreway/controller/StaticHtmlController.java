package com.wxmp.wxmoreway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("staticHtml")
public class StaticHtmlController {

    @GetMapping("/index")
    public String index(HttpServletRequest req, HttpServletResponse response){
        return "statichtml/index";
    }

    @GetMapping("/news")
    public String news(HttpServletRequest req, HttpServletResponse response){
        return "statichtml/news";
    }

    @GetMapping("/aboutOurs")
    public String aboutOurs(HttpServletRequest req, HttpServletResponse response){
        return "statichtml/aboutOurs";
    }

    @GetMapping("/programmeCentre")
    public String programmeCentre(HttpServletRequest req, HttpServletResponse response){
        return "statichtml/programmeCentre";
    }
}
