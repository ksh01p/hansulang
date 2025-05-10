package com.example.demo.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BoardController {

    @GetMapping("/board/create")
    public String createPage() {
        return "board/create";
    }

    @GetMapping("/board/list")
    public String listPage() {
        return "board/list";
    }

    @GetMapping("/board/detail/{id}")
    public String detailPage() {
        return "board/detail";
    }
}
