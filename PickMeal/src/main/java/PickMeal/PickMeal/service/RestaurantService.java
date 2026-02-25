package PickMeal.PickMeal.service;

import PickMeal.PickMeal.domain.Restaurant;
import PickMeal.PickMeal.dto.RestaurantDTO;
import PickMeal.PickMeal.mapper.RestaurantMapper; // 창고 관리자 연락처
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantMapper restaurantMapper; // 창고 관리자 연결

    public List<Restaurant> findAll() {
        // 가짜 데이터 생성 코드를 모두 지우고, 창고 관리자가 DB에서 꺼내온 진짜 데이터 리스트를 바로 전달합니다!
        return restaurantMapper.findAll();
    }
    public List<RestaurantDTO> getAllRestaurants() {
        // DB에서 식당 정보를 가져와 DTO 리스트로 변환하는 로직이 필요합니다.
        return restaurantMapper.findAllRestaurants();
    }

}

