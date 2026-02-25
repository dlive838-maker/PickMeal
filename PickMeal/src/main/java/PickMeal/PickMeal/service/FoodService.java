package PickMeal.PickMeal.service;

import PickMeal.PickMeal.domain.Food;
import PickMeal.PickMeal.mapper.FoodMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FoodService {

    @Autowired
    private FoodMapper foodMapper;

    // 1. static을 제거하고 mapper를 통해 DB에서 음식을 찾는 로직을 추가합니다.
    public Food findFoodByName(String name) {
        // mapper에 findByName 메서드가 있어야 합니다.
        // 없다면 foodMapper로 가서 해당 메서드를 정의해줘야 합니다.
        return foodMapper.findByName(name);
    }

    public Food drawCapsuleMenu(String category) {
        return foodMapper.getRandomFoodByCategory(category);
    }
}