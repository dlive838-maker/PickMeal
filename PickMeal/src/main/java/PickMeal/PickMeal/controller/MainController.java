package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.domain.Food;
import PickMeal.PickMeal.domain.User;
import PickMeal.PickMeal.service.UserService;
import PickMeal.PickMeal.domain.Restaurant;
import PickMeal.PickMeal.service.RestaurantService;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService; // 추가

    @GetMapping("/")
    public String index() {
        return "index"; // 인트로 화면
    }

    @GetMapping("/next-page") //
    public String next() {
        return "next-page";
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

    // 룰렛 돌리기 페이지
    @GetMapping("/roulette")
    public String roulettePage() {
        return "game/roulette";
    }

    @GetMapping("/twentyQuestions")
    public String twentyQuestionsPage() {
        return "game/twentyQuestions"; // templates/twentyQuestions.html 반환
    }

    @GetMapping("/capsule")
    public String goCapsulePage() {
        return "game/capsule";
    }

    @GetMapping("/users/forgot-pw")
    public String forgotPwPage() {
        return "users/forgot-pw";
    }

    @GetMapping("/board")
    public String boardPage() {
        return "board/board"; // templates/board.html 파일을 반환
    }

    @GetMapping("/hotplace") // 추가
    public String hotplacePage(Model model) { // 서빙 쟁반(Model)을 매개변수로 받아옵니다.

        // ① 주방장(Service)에게 맛집 리스트 전체를 요리해 오라고 시킵니다.
        List<Restaurant> restaurantList = restaurantService.findAll();

        // ② 가져온 요리(데이터)를 'restaurantList'라는 이름표를 붙여 쟁반(Model)에 담습니다.
        // 이렇게 해야 hotplace.html 화면에서 자바스크립트가 이 이름표를 보고 핀을 그릴 수 있습니다.
        model.addAttribute("restaurantList", restaurantList);

        // ③ 요리가 담긴 쟁반을 들고 손님 테이블(hotplace.html)로 나갑니다.
        return "hotplace";
    } // templates/hotplace.html 파일 반환

    @GetMapping("/game")
    public String gamePage() {
        return "game/game";
    }

    @GetMapping("/worldcup/setup") // 이 주소로 들어오면
    public String gameSetupPage() {
        return "game/game_setup"; // game_setup.html 파일을 보여줍니다.
    }

    @GetMapping("/worldcup")
    public String worldcupRedirect() {
        // [비유] 손님이 /worldcup으로 들어오면, "설정 화면으로 모실게요~" 하고 방향을 돌려주는(redirect) 역할입니다.
        return "redirect:/worldcup/setup";
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



