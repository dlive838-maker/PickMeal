package PickMeal.PickMeal.domain;

import lombok.Data;

@Data
public class Food {
    private Long foodId;      // DB의 bigint와 매칭 (Long 권장)
    private String foodName;  // 음식 이름
    private String category;  // 한식, 중식 등
    private String description; // 설명 (있으니까 추가!)
    private String imagePath; // ★중요: imgPath가 아니라 imagePath입니다!
    private int winCount;     // 우승 횟수
}