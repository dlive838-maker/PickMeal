package PickMeal.PickMeal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Getter, Setter, toString 등을 자동 생성
@NoArgsConstructor // 파라미터가 없는 기본 생성자 생성 (new PlaceStatsDto() 가능하게 함)
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자 생성
public class PlaceStatsDto {
    private String kakaoPlaceId;
    private int viewCount;
    private int heartCount;
    private int reviewCount;
}