package PickMeal.PickMeal.mapper;

import PickMeal.PickMeal.domain.Questions;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuestionsMapper {
    String getFinalQuestion();

    Questions getNextValidQuestion(List<Integer> askedQuestionIds);

    Questions getQuestionsByAttributeName(String category);
}
