package PickMeal.PickMeal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    // 1. 메인 페이지 호출 (localhost:8080/)
    @GetMapping("/")
    public String index() {
        // templates/index.html을 찾아 브라우저에 렌더링합니다.
        return "index";
    }

    // 2. 음식 리스트 페이지 호출 (localhost:8080/next)
    @GetMapping("/next")
    public String next() {
        // templates/next-page.html을 찾아 브라우저에 렌더링합니다.
        return "next-page";
    }

    @GetMapping("/game")
    public String gamePage() {
        return "game"; // templates/game.html 파일을 찾아서 보여줌
    }


    @GetMapping("/board")
    public String boardPage() {
        return "board"; // templates/board.html 파일을 반환
    }


    @GetMapping("/mypage")
    public String mypage() {
        return "mypage"; // templates/mypage.html 반환
    }

    @GetMapping("/roulette")
    public String roulettePage() {
        return "roulette"; // templates/roulette.html 반환
    }

    @GetMapping("/forgot-pw")
    public String forgotPwPage() {
        return "forgot-pw"; // templates/forgot-pw.html 파일을 반환
    }

    @GetMapping("/board/write")
    public String boardWritePage() {
        return "board-write"; // templates/board-write.html 파일 반환
    }

    @GetMapping("/hotplace")
    public String hotplacePage() {
        return "hotplace"; // templates/hotplace.html 파일 반환
    }

}
