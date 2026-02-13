package PickMeal.PickMeal.mapper;

import org.apache.ibatis.annotations.Mapper; // 이 줄이 있어야 합니다!
import PickMeal.PickMeal.dto.FoodDTO;
import java.util.List;

@Mapper // 👈 이 이름표(Annotation)가 핵심!
public interface FoodMapper {
    List<FoodDTO> getFoodList();
    List<FoodDTO> getRandomFood32();
}