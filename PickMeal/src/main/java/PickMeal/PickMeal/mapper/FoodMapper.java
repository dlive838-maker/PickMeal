package PickMeal.PickMeal.mapper;

import PickMeal.PickMeal.domain.Food;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FoodMapper {
    // XML의 id="getRandomFoodByCategory"와 이름을 맞춥니다.
    // @Param("category")를 통해 XML의 #{category}에 값이 전달됩니다.
    Food getRandomFoodByCategory(@Param("category") String category);
}