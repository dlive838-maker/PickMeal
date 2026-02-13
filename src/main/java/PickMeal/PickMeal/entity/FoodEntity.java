package PickMeal.PickMeal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity // 이 클래스가 DB의 'food' 테이블이 된다.
@Getter @Setter
@NoArgsConstructor
@Table(name = "food") // DB에 생성될 테이블 이름
public class FoodEntity {

    @Id // 음식마다 붙는 고유 번호 (주민번호 같은 것)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "foodId")
    private Long foodId;

    @Column(name = "foodName", nullable = false)
    private String foodName; // 음식 이름 (예: 치킨, 피자)

    @Column(name = "imagePath", nullable = false, length = 500)
    private String imagePath; // 사진 파일이 있는 위치

    @Column(name = "winCount")
    private Integer winCount = 0;
}