package PickMeal.PickMeal.mapper;

import PickMeal.PickMeal.domain.Food;
import PickMeal.PickMeal.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserMapper {

    User findById(String id);

    void save(User user);

    void edit(User user);

    void updateEmail(User user);

    void updatePassword(User user);

    void updateStatus(@Param("user_id") Long user_id, @Param("status") String status);

    int countByNickname(String nickname);

    // ★ 추가: 음식 번호(id)를 주면 DB에서 우승 횟수를 1 증가시키는 주문서입니다.
    void incrementFoodWinCount(Long foodId);

    // [월드컵용] 여러 카테고리를 섞어서 랜덤으로 가져오는 메서드
    List<Food> getMixedFoods(@Param("types") List<String> types, @Param("round") int round);

    // ★ 추가: 우승 횟수가 높은 순서대로 10개를 가져오는 주문서입니다.
    List<Food> getTop10Foods();

    void updateWinCount(Long id);
}
