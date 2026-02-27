package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.domain.Admin;
import PickMeal.PickMeal.domain.Food;
import PickMeal.PickMeal.domain.Game;
import PickMeal.PickMeal.domain.User;
import PickMeal.PickMeal.mapper.AdminMapper;
import PickMeal.PickMeal.service.FoodService;
import PickMeal.PickMeal.service.GameService;
import PickMeal.PickMeal.service.ReviewService;
import PickMeal.PickMeal.service.UserService;
import PickMeal.PickMeal.service.RestaurantService;
import org.springframework.security.core.Authentication;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
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
    private final ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService; // 추가

    @Autowired
    private FoodService foodService;

    @Autowired
    private GameService gameService;

    @Autowired
    private AdminMapper adminMapper;

    @GetMapping("/")
    public String index() {
        return "index"; // 인트로 화면
    }

    @GetMapping("/next-page") //
    public String next(Model model) {
//        List<RestaurantDTO> popularRestList = reviewWishService.getPopularRest();
//        model.addAttribute("popularRestList", popularRestList);
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

            // 2. game 객체 생성 및 초기화
            Game game = new Game();
            game.setUser_id(null);
            game.setAdmin_id(null);
            game.setFood_id(foodId);
            game.setGameType(gameType);
            game.setPlayDate(LocalDateTime.now());

            // 3. 로그인 사용자 정보 처리
            if (authentication != null && authentication.isAuthenticated()) {
                String loginName = authentication.getName();
                boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                if (isAdmin) {
                    // 관리자 로직
                    Admin admin = adminMapper.findByLoginId(loginName);
                    if (admin != null) {
                        game.setAdmin_id(admin.getAdminId());
                    }
                } else {
                    // 일반 유저 로직
                    String registrationId = "";
                    if (authentication instanceof org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken token) {
                        registrationId = token.getAuthorizedClientRegistrationId();
                    }

                    String fullUserId = (registrationId == null || registrationId.isEmpty() || loginName.startsWith(registrationId))
                            ? loginName : registrationId + "_" + loginName;

                    System.out.println("검색하려는 ID: " + fullUserId);
                    User user = userService.findById(fullUserId);

                    if (user != null) {
                        System.out.println("유저 찾기 성공! PK: " + user.getUser_id());
                        game.setUser_id(user.getUser_id());
                    } else {
                        System.out.println("유저 찾기 실패... DB를 확인하세요.");
                    }
                }
            } // <--- authentication 체크 블록 끝

            // [중요] 0 방어 로직 (항상 실행되도록 블록 밖으로 뺌)
            if (game.getUser_id() != null && game.getUser_id() == 0L) {
                game.setUser_id(null);
            }

            // [핵심] 로그인 여부와 상관없이 게임 기록 저장 시도
            gameService.insertGameRecord(game);
            return "success";

        } catch (Exception e) { // <--- try 블록 끝 및 catch 시작
            e.printStackTrace();
            return "fail";
        }
    } // <--- 메서드 끝

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



