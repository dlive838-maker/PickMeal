package PickMeal.PickMeal.service;

import PickMeal.PickMeal.domain.Restaurant;
import PickMeal.PickMeal.dto.RestaurantDTO;
import PickMeal.PickMeal.mapper.RestaurantMapper; // 창고 관리자 연락처
import PickMeal.PickMeal.mapper.ReviewWishMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantMapper restaurantMapper; // 창고 관리자 연결
    private final ReviewWishMapper wishMapper;

    public List<RestaurantDTO> findAll() {
        // 가짜 데이터 생성 코드를 모두 지우고, 창고 관리자가 DB에서 꺼내온 진짜 데이터 리스트를 바로 전달합니다!
        return restaurantMapper.findAll();
    }

    // RestaurantService.java
    @Transactional
    public void saveWish(Long restId, Long userPk) { // [수정] String userId를 Long userPk로 변경
        // 이제 매퍼에 숫자를 던져주므로 에러가 사라집니다.
        wishMapper.insertWish(restId, userPk);
    }

}

