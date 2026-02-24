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
            response.setNextQuestion_id(nextQuestion.getQuestion_id());
            response.setNextQuestion_text(nextQuestion.getQuestion_text());
            response.setNextAttribute_name(nextQuestion.getAttribute_name());
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