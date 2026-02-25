package PickMeal.PickMeal.dto;

import lombok.Data;

@Data
public class RestaurantDTO {
    private Long restId;      // 식당 고유 번호 (PK)
    private String restName;    // 식당 이름
    private String restAddress; // 식당 주소
    private String restMenu;    // 대표 메뉴
    private String restPrice;   // 가격대
    private String restImg;     // 식당 사진 URL
    private Double restLat;     // 지도 위도 (Latitude) - 지도를 찍을 때 필수!
    private Double restLng;     // 지도 경도 (Longitude) - 지도를 찍을 때 필수!
    private int restViews;      // 새로 추가된 조회수
}
