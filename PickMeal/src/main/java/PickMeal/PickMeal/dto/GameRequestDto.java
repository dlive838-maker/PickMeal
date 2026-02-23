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

    private Integer is_spicy;
    private Integer is_soup;
    private Integer is_fried;
    private Integer is_roasted;
    private Integer has_pork;
    private Integer has_beef;
    private List<Integer> askedQuestionIds;
}
