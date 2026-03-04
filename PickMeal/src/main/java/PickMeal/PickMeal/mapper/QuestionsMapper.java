package PickMeal.PickMeal.mapper;

import PickMeal.PickMeal.domain.Questions;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param; // 이 줄을 추가하세요.

import java.util.List;

@Mapper
public interface QuestionsMapper {
    String getFinalQuestion();

    // [중요] XML의 #{category}와 이름을 강제로 맞춥니다.
    Questions getQuestionsByAttributeName(@Param("category") String category);
}