package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.domain.User;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String index() {
        return "index"; // 인트로 화면
    }

    @GetMapping("/next-page") //
    public String next() {
        return "next-page";
    }

    @GetMapping("/game")
    public String gamePage() {
        return "game/game"; // templates/game.html 파일을 찾아서 보여줌
    }


    @GetMapping("/board")
    public String boardPage() {
        return "board/board"; // templates/board.html 파일을 반환
    }


    @GetMapping("/mypage")
    public String mypage(@AuthenticationPrincipal User user, Model model) {
        // 세션에 저장된 현재 로그인 유저 정보(@AuthenticationPrincipal)를 모델에 담습니다.
        // 만약 User 객체가 null이면 로그인 페이지로 보내거나 예외 처리를 해야 합니다.
        if (user == null) {
            return "redirect:/users/login";
        }

        model.addAttribute("user", user); // 'user'라는 이름으로 객체를 넘겨줌
        return "users/mypage";
    }
    @GetMapping("/roulette")
    public String roulettePage() {
        return "game/roulette"; // templates/roulette.html 반환
    }

    @GetMapping("/twentyQuestions")
    public String twentyQuestionsPage() {
        return "game/twentyQuestions"; // templates/twentyQuestions.html 반환
    }

    @GetMapping("/forgot-pw")
    public String forgotPwPage() {
        return "users/forgot-pw"; // templates/forgot-pw.html 파일을 반환
    }

    @GetMapping("/board/write")
    public String boardWritePage() {
        return "board/board-write"; // templates/board-write.html 파일 반환
    }

    @GetMapping("/hotplace")
    public String hotplacePage() {
        return "hotplace"; // templates/hotplace.html 파일 반환
    }
}