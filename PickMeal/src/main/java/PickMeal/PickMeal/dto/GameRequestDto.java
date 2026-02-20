package PickMeal.PickMeal.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GameRequestDto {
    private Integer category_korean;
    private Integer category_western;
    private Integer category_chinese;
    private Integer category_japanese;
    private Integer category_asian;

    private Integer isSpicy;
    private Integer isSoup;
    private Integer isFried;
    private Integer isRoasted;
    private Integer hasPork;
    private Integer hasBeef;
    private List<Integer> askedQuestionIds;
}
