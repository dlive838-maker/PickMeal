package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.domain.Questions;
import PickMeal.PickMeal.dto.GameRequestDto;
import PickMeal.PickMeal.dto.GameResponseDto;
import PickMeal.PickMeal.service.TwentyQuestionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/twenty-questions")
@RequiredArgsConstructor
public class TwentyQuestionsController {

    private final TwentyQuestionsService twentyQuestionsService;


    @PostMapping("/next")
    public ResponseEntity<GameResponseDto> getNextStep(@RequestBody GameRequestDto request) {

        GameResponseDto response = new GameResponseDto();

        List<String> remainingFoods = twentyQuestionsService.getFilteredFoods(request);

        if (remainingFoods.size() <= 3 && remainingFoods.size() > 0) {
            String finalQuestion = twentyQuestionsService.getFinalQuestion();
            response.setStatus("FINAL_CHOICE");
            response.setRemain_foodList(remainingFoods);
            response.setNextQuestion_text(finalQuestion);

        } else if (remainingFoods.isEmpty()) {
            response.setStatus("NO_FOOD");
        } else {
            response.setStatus("QUESTION");
            Questions nextQuestion = twentyQuestionsService.getNextValidQuestion((request));

            // 🌟 [수정 포인트] 안전장치 추가!
            // 주방장이 다음 질문지를 제대로 가져왔는지(null이 아닌지) 확인합니다.
            if (nextQuestion != null) {
            response.setNextQuestion_id(nextQuestion.getQuestion_id());
            response.setNextQuestion_text(nextQuestion.getQuestion_text());
            response.setNextAttribute_name(nextQuestion.getAttribute_name());
        }   else {
                // 더 이상 물어볼 질문이 없는데 음식은 아직 많이 남은 경우입니다.
                // 이럴 때는 "결과가 없어요" 혹은 "남은 음식 중에 골라보세요"로 상태를 바꿉니다.
                response.setStatus("NO_MORE_QUESTIONS");
                response.setRemain_foodList(remainingFoods);
            }
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/food/imagePath")
    public String getImagePath(@RequestParam("foodName") String foodName) {
        // DB에서 음식 이름으로 imagePath를 찾아오는 메서드 실행
        String imagePath = twentyQuestionsService.findImagePathByName(foodName);
        return imagePath;
    }
}