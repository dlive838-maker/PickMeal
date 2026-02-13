package PickMeal.PickMeal.service;

import PickMeal.PickMeal.dto.FoodDTO;
import PickMeal.PickMeal.mapper.FoodMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import PickMeal.PickMeal.repository.FoodRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import PickMeal.PickMeal.entity.FoodEntity;

@Service
public class FoodService {

    @Autowired // Mapper를 자동으로 불러와서 연결한다.
    private FoodMapper foodMapper;

    @Autowired // 스프링아, 냉장고 장부(Repository)를 자동으로 가져와서 연결해줘!
    private FoodRepository foodRepository; // 👈 이 줄이 있어야 보조가 장부를 손에 쥠.

    @Transactional // [추가] 점수 올리는 도중 사고가 나지 않게 안전하게 처리함.
    public void updateWinCount(Long foodId) {
        // [추가] 장부(Repository)에 가서 이 음식의 우승 횟수를 1 올려달라고 시킴.
        foodRepository.incrementWinCount(foodId);
    }

    // "랜덤으로 32개 음식을 가져와서 나열해줘!"라고 명령.
    public List<FoodDTO> getGameItems() {
        return foodMapper.getRandomFood32();
    }

    public List<FoodEntity> getTop10Foods() {
        // Repository에서 가져온 데이터(FoodEntity)를 그대로 돌려줌.
        return foodRepository.findTop10ByOrderByWinCountDesc();
    }

        public List<FoodEntity> getFoodsForWorldCup(String category, int round) {
            // 1. 지배인에게 받은 주문(카테고리, 강수)을 들고 주방장(Repository)에게 갑니다.
            // 2. 주방장이 DB에서 골라낸 음식 목록을 그대로 받아서 지배인에게 전달합니다.
            return foodRepository.findRandomFoodsByCategory(category, round);
        }
    }
