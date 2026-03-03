package PickMeal.PickMeal.service;

import PickMeal.PickMeal.dto.RestaurantDTO;
import PickMeal.PickMeal.mapper.RestaurantMapper;
import PickMeal.PickMeal.mapper.ReviewWishMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantMapper restaurantMapper;
    private final ReviewWishMapper wishMapper;

    public List<RestaurantDTO> findAll() {
        return restaurantMapper.findAll();
    }

    @Transactional
    // [수정] Long userPk를 String userId로 변경합니다.
    // 우리 프로젝트는 이제 'woals106' 같은 문자열 아이디를 사용하기 때문입니다.
    public void saveWish(Long restId, String userId) {
        // 매퍼 인터페이스도 String을 받도록 고쳤으므로 이제 에러가 사라집니다.
        wishMapper.insertWish(restId, userId);
    }
}