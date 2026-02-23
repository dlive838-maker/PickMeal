package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.domain.User;
import PickMeal.PickMeal.service.UserService;
import PickMeal.PickMeal.domain.Food;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List; // List를 사용하기 위해 꼭 필요합니다!

@Controller
public class MainController {
    // MainController 클래스 바로 아래에 추가
    @Autowired
    private UserService userService; // 이제 주방장이 userService라는 일꾼을 쓸 수 있게 됩니다!

    @GetMapping("/")
    public String index() {
        return "index"; // 인트로 화면
    }

    @GetMapping("/next")
    public String next() {
        return "next-page"; // 메뉴 슬라이드 화면
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

    // MainController.java

    @GetMapping("/worldcup/setup") // 이 주소로 들어오면
    public String gameSetupPage() {
        return "game/game_setup"; // game_setup.html 파일을 보여줍니다.
    }

    @GetMapping("/game/play")
    public String playWorldCup(
            @RequestParam(value = "types") List<String> types, // 'value'를 명시해주면 더 정확합니다.
            @RequestParam(value = "round") int round,
            Model model) {

        // 1. 서비스 일꾼에게 여러 종류(types)와 몇 강(round)인지 전달하며 재료를 가져오라고 시킵니다.
        // (여기서 userService가 빨간 줄이면 위에서 @Autowired로 선언했는지 꼭 확인하세요!)
        List<Food> foods = userService.getMixedFoods(types, round);

        // 2. 주방장이 준비한 재료들을 게임판(HTML)으로 전달합니다.
        model.addAttribute("foods", foods);
        model.addAttribute("totalRound", round);

        // 3. 실제 대결이 펼쳐질 'worldcup.html'로 이동합니다.
        return "game/worldcup";
    }
    // MainController.java 에 추가
    @GetMapping("/worldcup/ranking")
    public String rankingPage(Model model) {
        // 1. 서비스(일꾼)에게 DB에서 인기 음식 10개를 가져오라고 시킵니다.
        // (이미 UserService에 getTop10Foods를 만드셨다면 바로 사용!)
        List<Food> rankingList = userService.getTop10Foods();

        // 2. 가져온 '진짜 데이터'를 'rankingList'라는 이름으로 접시에 담습니다.
        model.addAttribute("rankingList", rankingList);

        // 3. 데이터가 담긴 접시를 들고 ranking.html로 이동합니다.
        return "game/ranking";
    }

    @PostMapping("/worldcup/win/{foodId}")
    @ResponseBody // [비유] 화면 이동 없이 "기록 완료!"라는 짧은 메시지만 보냅니다.
    public String updateWinCount(@PathVariable("foodId") Long foodId) {
        try {
            // 일꾼(Service)에게 해당 ID 음식의 우승 횟수를 1 증가시키라고 시킵니다.
            userService.updateFoodWinCount(foodId);
            return "success";
        } catch (Exception e) {
            return "fail";
        }
    }


    }
