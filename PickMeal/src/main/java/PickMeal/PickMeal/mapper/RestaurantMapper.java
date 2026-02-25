package PickMeal.PickMeal.mapper;

import PickMeal.PickMeal.domain.Restaurant;
import PickMeal.PickMeal.dto.RestaurantDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper // 스프링 본사에게 이 파일이 DB와 통신하는 '창고 관리자'임을 알립니다.
public interface RestaurantMapper {

    // 대형 냉장고(MySQL)의 'restaurant' 테이블에서 모든 데이터를 가져오는 마법의 주문입니다.
//    @Select("SELECT * FROM restaurant")
    List<Restaurant> findAll();

    List<RestaurantDTO> findAllRestaurants();

    // [추가] 조회수를 1씩 올리는 기능을 기사님(Mapper)에게 알려줍니다.
    void updateViewCount(Long resId);

}