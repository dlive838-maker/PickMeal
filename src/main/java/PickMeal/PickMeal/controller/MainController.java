package PickMeal.PickMeal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String index() {
        return "index"; // 인트로 화면
    }

    @GetMapping("/next")
    public String next() {
        return "next-page"; // 메뉴 슬라이드 화면
    }
}