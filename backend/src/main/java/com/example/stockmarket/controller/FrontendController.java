package com.example.stockmarket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {

    @RequestMapping(value = {
            "/", "/stock", "/student/**", "/teacher/**"
    })
    public String forward() {
        return "forward:/index.html";
    }
}