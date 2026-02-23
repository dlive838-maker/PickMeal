package PickMeal.PickMeal.mapper;

import org.apache.ibatis.annotations.Mapper;
import PickMeal.PickMeal.dto.FoodDTO;
import java.util.List;

@Mapper
public interface FoodMapper {
    void getFoodInfo();
    List<FoodDTO> getFoodList();
    List<FoodDTO> getRandomFood32();
}