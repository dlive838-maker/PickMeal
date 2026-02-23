package PickMeal.PickMeal.repository;

import PickMeal.PickMeal.entity.FoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

@Repository // 1. 이 이름표가 '바깥쪽'에 하나만 있어야 한다.
public interface FoodRepository extends JpaRepository<FoodEntity, Long> {

    @Query(value = "SELECT * FROM food " +
            "WHERE (:category = 'all' OR category = :category) " + //  카테고리 필터링
            "ORDER BY RAND() LIMIT :round", nativeQuery = true)     // 선택한 강수만큼 랜덤 추출
    List<FoodEntity> findRandomFoodsByCategory(@Param("category") String category, @Param("round") int round);
    List<FoodEntity> findTop10ByOrderByWinCountDesc();

    // 우승 점수를 1점 올리는 주문.
    @Modifying // 데이터를 수정(Update)할 때 꼭 필요한 이름표.
    @Transactional // DB에 영구적으로 기록하겠다는 약속.
    @Query("UPDATE FoodEntity f SET f.winCount = f.winCount + 1 WHERE f.foodId = :id")
    void incrementWinCount(Long id);


}