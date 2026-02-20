package PickMeal.PickMeal.service;

import PickMeal.PickMeal.domain.Questions;
import PickMeal.PickMeal.dto.GameRequestDto;
import PickMeal.PickMeal.mapper.FoodMapper;
import PickMeal.PickMeal.mapper.QuestionsMapper;
import PickMeal.PickMeal.mapper.TwentyAttributeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TwentyQuestionsService {
    private final QuestionsMapper questionsMapper;
    private final FoodMapper foodMapper;
    private final TwentyAttributeMapper twentyAttributeMapper;

    public Questions getQuestions(Questions attributeName) {
        return new Questions();
    }

    public List<String> getFilteredFoods(GameRequestDto request) {
        return foodMapper.getFilteredFoods(request);
    }

    public String getFinalQuestion() {
        return questionsMapper.getFinalQuestion();
    }

    public Questions getNextValidQuestion(GameRequestDto request) {

        if (request.getCategory_korean() == null) {
            return questionsMapper.getQuestionsByAttributeName("category_korean");
        }
        if (request.getCategory_korean() == 1) {
            return getNextAttributeQuestion(request);
        }

        if (request.getCategory_western() == null) {
            return questionsMapper.getQuestionsByAttributeName("category_western");
        }
        if (request.getCategory_western() == 1) {
            return getNextAttributeQuestion(request);
        }

        if (request.getCategory_chinese() == null) {
            return questionsMapper.getQuestionsByAttributeName("category_chinese");
        }
        if (request.getCategory_chinese() == 1) {
            return getNextAttributeQuestion(request);
        }

        if (request.getCategory_japanese() == null) {
            return questionsMapper.getQuestionsByAttributeName("category_japanese");
        }
        if (request.getCategory_japanese() == 1) {
            return getNextAttributeQuestion(request);
        }


        return getNextAttributeQuestion(request);
    }

    private Questions getNextAttributeQuestion(GameRequestDto request) {

        if (request.getIsSoup() == null) return questionsMapper.getQuestionsByAttributeName("is_soup");
        if (request.getIsSpicy() == null) return questionsMapper.getQuestionsByAttributeName("is_spicy");
        if (request.getIsFried() == null) return questionsMapper.getQuestionsByAttributeName("is_fried");
        if (request.getIsRoasted() == null) return questionsMapper.getQuestionsByAttributeName("is_roasted");
        if (request.getHasPork() == null) return questionsMapper.getQuestionsByAttributeName("has_pork");
        if (request.getHasBeef() == null) return questionsMapper.getQuestionsByAttributeName("has_beef");

        return questionsMapper.getQuestionsByAttributeName("final_recommendation");
    }
}
