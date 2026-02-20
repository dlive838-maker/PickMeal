package PickMeal.PickMeal.mapper;

import PickMeal.PickMeal.dto.GameRequestDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FoodMapper {
    void getFoodInfo();

    List<String> getFilteredFoods(GameRequestDto request);
}