package PickMeal.PickMeal.mapper;

import PickMeal.PickMeal.dto.RestaurantDTO;
import PickMeal.PickMeal.dto.ReviewWishDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReviewWishMapper {
    // 찜과 리뷰를 저장하거나 업데이트하는 쿼리 (Upsert 방식)
    void saveInteraction(ReviewWishDTO dto);

    // 특정 식당의 찜 상태만 가져오기
    Integer getWishStatus(ReviewWishDTO dto);

    List<RestaurantDTO> getPopularRest();
}