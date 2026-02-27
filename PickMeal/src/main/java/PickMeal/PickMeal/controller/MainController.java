package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.domain.Food;
import PickMeal.PickMeal.domain.Game;
import PickMeal.PickMeal.domain.User;
import PickMeal.PickMeal.dto.RestaurantDTO;
import PickMeal.PickMeal.service.FoodService;
import PickMeal.PickMeal.service.GameService;
import PickMeal.PickMeal.service.ReviewWishService;
import PickMeal.PickMeal.service.UserService;
import PickMeal.PickMeal.domain.Restaurant;
import PickMeal.PickMeal.service.RestaurantService;
import org.springframework.security.core.Authentication;
import lombok.RequiredArgsConstructor;
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

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final ReviewWishService reviewWishService;

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService; // 추가

    @Autowired
    private FoodService foodService;

    @Autowired
    private GameService gameService;

    @GetMapping("/")
    public String index() {
        return "index"; // 인트로 화면
    }

    @GetMapping("/next-page") //
    public String next(Model model) {
        List<RestaurantDTO> popularRestList = reviewWishService.getPopularRest();
        model.addAttribute("popularRestList", popularRestList);
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

    @GetMapping("/api/food/image")
    @ResponseBody
    public String getFoodImage(@RequestParam("name") String name) {
        Food food = foodService.findFoodByName(name);

        // DB의 imagePath에 이미 "/images/Korean food/..." 가 들어있으므로 그대로 반환합니다.
        if (food != null && food.getImagePath() != null) {
            return food.getImagePath().trim();
        }

        return "/images/meal.png";
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
    @ResponseBody
    public String updateWinCount(@PathVariable("foodId") Long foodId,
                                 @RequestParam(value="gameType", defaultValue="worldcup") String gameType,
                                 Authentication authentication) {
        try {
            // 1. 기존 전체 카운트 증가
            userService.updateFoodWinCount(foodId);

            // 2. game 테이블 상세 기록 저장
            Game game = new Game();
            game.setFood_id(foodId);
            game.setGameType(gameType);
            game.setPlayDate(LocalDateTime.now()); // java.time.LocalDateTime

            // 3. 로그인 사용자 정보 처리
            if (authentication != null && authentication.isAuthenticated()) {
                String userId = authentication.getName(); // 기본 ID 추출

                // 소셜 로그인 접두어 처리 로직
                String registrationId = "";
                if (authentication instanceof org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) {
                    registrationId = ((org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
                }

                // DB 조회용 fullUserId 조합 (예: kakao_4746951582)
                String fullUserId = (registrationId == null || registrationId.isEmpty() || userId.startsWith(registrationId))
                        ? userId : registrationId + "_" + userId;

                // DB에서 유저 객체를 찾아 PK(숫자)를 가져옵니다.
                User user = userService.findById(fullUserId);
                if (user != null) {
                    game.setUser_id(user.getUser_id()); // Long 타입 PK 저장
                }
            }

            gameService.insertGameRecord(game);
            return "success";
        } catch (Exception e) {
            e.printStackTrace(); // 빨간 줄 대신 로그를 남겨서 확인
            return "fail";
        }
    }

    @GetMapping("/api/food/getIdByName")
    @ResponseBody
    public Long getFoodIdByName(@RequestParam("foodName") String foodName) {
        Food food = foodService.findFoodByName(foodName);

        if (food != null) {
            return food.getFoodId(); // 음식의 PK(숫자 ID) 반환
        }
        return null; // 찾지 못했을 경우
    }

}



