package PickMeal.PickMeal.service;

import PickMeal.PickMeal.domain.Food;
import PickMeal.PickMeal.mapper.FoodMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FoodService {

    @Autowired
    private FoodMapper foodMapper;

    public Food drawCapsuleMenu(String category) {
        // DB가 이미 랜덤으로 딱 1개만 골라서 주므로, 받은 결과를 바로 컨트롤러에 전달합니다.
        return foodMapper.getRandomFoodByCategory(category);
    }
}
