package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.domain.Questions;
import PickMeal.PickMeal.dto.GameRequestDto;
import PickMeal.PickMeal.dto.GameResponseDto;
import PickMeal.PickMeal.service.TwentyQuestionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/twenty-questions")
@RequiredArgsConstructor
public class TwentyQuestionsController {

    private final TwentyQuestionsService twentyQuestionsService;

    @PostMapping("/next")
    public ResponseEntity<GameResponseDto> getNextStep(@RequestBody GameRequestDto request) {

        GameResponseDto response = new GameResponseDto();

        // 1. MyBatis로 현재 조건에 맞는 남은 음식 목록 조회
        List<String> remainingFoods = twentyQuestionsService.getFilteredFoods(request);

        // 2. 남은 음식 개수에 따른 분기 (3개 이하일 때 강제 종료!)
        if (remainingFoods.size() <= 3 && remainingFoods.size() > 0) {
            String finalQuestion = twentyQuestionsService.getFinalQuestion();
            response.setStatus("FINAL_CHOICE");
            response.setRemain_foodList(remainingFoods);
            response.setNextQuestion_text(finalQuestion);
        } else if (remainingFoods.isEmpty()) {
            response.setStatus("NO_FOOD"); // 맞는 음식이 없을 때의 예외 처리
        } else {
            response.setStatus("QUESTION");
            // 아직 안 물어본 질문 중 하나를 DB에서 가져오는 로직 (서비스 단에서 처리)
            Questions nextQuestion = twentyQuestionsService.getNextValidQuestion((request));
            response.setNextQuestion_id(String.valueOf(nextQuestion.getQuestion_id()));
            response.setNextQuestion_text(nextQuestion.getQuestion_text());
            response.setNextAttribute_name(nextQuestion.getAttribute_name());
        }

        return ResponseEntity.ok(response);
    }
}