package PickMeal.PickMeal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GamePageController {

    // 게임 선택 페이지
    @GetMapping("/game")
    public String goGamePage() {
        return "game/game";
    }

    // 룰렛 돌리기 페이지
    @GetMapping("/roulette")
    public String roulettePage() {
        return "game/roulette";
    }

    // 캡슐 뽑기 페이지
    @GetMapping("/capsule")
    public String goCapsulePage() {
        return "game/capsule";
    }
}