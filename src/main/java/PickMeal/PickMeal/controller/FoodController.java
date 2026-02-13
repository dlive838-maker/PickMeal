package PickMeal.PickMeal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import PickMeal.PickMeal.service.FoodService;
import PickMeal.PickMeal.dto.FoodDTO;
import java.util.List;
import java.util.Map;
import PickMeal.PickMeal.repository.FoodRepository;
import PickMeal.PickMeal.entity.FoodEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller // 이 클래스가 웹 요청을 처리하는 '주방장'임을 스프링에게 알려줌
public class FoodController {
    // 1. 냉장고(Repository)를 사용하겠다고 이름을 등록한다.
    private final FoodRepository foodRepository;

    // 2. 스프링이 실행될 때 냉장고를 이 주방장에게 전달해 준다. (생성자 주입)
    public FoodController(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    @Autowired // 요리 재료인 FoodService를 자동으로 가져옴
    private FoodService foodService;

    @GetMapping("/game")
    public String startGame(Model model) {
        // DB에서 무작위로 32개의 음식을 한꺼번에 가져온다!
        // findRandom32Foods()는 아까 Repository에 만들었던 주문.
        List<FoodEntity> foodList = foodRepository.findRandom32Foods();

        // 2. 32명의 선수 명단을 쟁반(Model)에 담아 game.html로 보낸다.
        // 이름을 'foodList'로 통일하면 자바스크립트에서 쓰기 편함.
        model.addAttribute("foodList", foodList);

        return "game";
    }

        @PostMapping("/game/win") // 👈 HTML에서 보낸 신호를 받는 통로 이름.
        @ResponseBody // 👈 화면 이동 없이 "성공" 메시지만 전달.
        public String saveWinner(@RequestParam("id") Long foodId) {
            // 주방 보조(Service)에게 해당 음식의 점수를 1 올리라고 지시.
            foodService.updateWinCount(foodId);
            return "success";
        }

    @GetMapping("/ranking") // 🚀 localhost:8080/ranking 주소로 들어오면 실행.
    public String showRanking(Model model) {
        // 1. 서비스에게 랭킹 TOP 10 명단을 가져오라고 시킨다.
        List<FoodEntity> rankList = foodService.getTop10Foods();

        // 2. 가져온 명단을 'rankList'라는 이름표를 붙여 화면(HTML)으로 보낸다.
        model.addAttribute("rankList", rankList);

        // 3. ranking.html 파일을 화면에 띄워준다.
        return "ranking";
    }

    }


