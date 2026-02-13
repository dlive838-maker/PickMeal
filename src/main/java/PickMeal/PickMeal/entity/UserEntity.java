package PickMeal.PickMeal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity // 이 클래스가 DB 테이블과 연결된 '실체(Entity)'임을 선언.
@Getter // 데이터를 가져오는 메소드를 자동으로 만듬.
@Setter // 데이터를 저장하는 메소드를 자동으로 만듬.
@NoArgsConstructor // 파라미터가 없는 기본 생성자를 만듬.
@Table(name = "user") // 실제 DB에 생성될 테이블 이름을 'user'로 지정.
public class UserEntity {

    @Id // 이 필드가 테이블의 'PK(기본키, 주민등록번호 같은 것)'가 됨.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 번호를 1, 2, 3... 순서대로 자동 생성.
    private Long id;

    @Column(nullable = false, unique = true) // 비어있으면 안 되고(null 불가), 중복될 수 없다.
    private String email; // 네이버에서 받은 고유 이메일

    @Column(nullable = false, unique = true)
    private String username; // 👈 DB의 'username' 칸과 연결될 필드!

    @Column(nullable = false)
    private String name; // 네이버에서 받은 실명 (황준호님!)

    @Column(name = "socialLoginSite") // 👈 DB의 실제 칸 이름이 'socialLoginSite'라고 알려준다!
    private String provider; // 'naver'라고 저장하여 어디서 온 회원인지 기록한다.

    private String nickname;

    private String gender;

    @Column(name = "phoneNumber") // DB 컬럼명과 변수명이 다를 경우 명시 (낙타표기법 주의)
    private String phoneNumber;

    private String birthDate;

    @Column(name = "socialId") // DB의 socialId 컬럼과 연결.
    private String socialId;

    private String likeMenu;
    private String disLikeMenu;

    // 회원가입 처리를 쉽게 하기 위한 생성자
    public UserEntity(String email, String name, String provider, String nickname, String gender, String phoneNumber, String birthDate, String socialId) {
        this.email = email;
        this.username = email; // 👈 이메일을 아이디(username)로도 사용한다!
        this.name = name;
        this.provider = provider;
        this.nickname = nickname; // 추가!
        this.gender = gender;
        this.phoneNumber = phoneNumber; //
        this.birthDate = birthDate;
        this.socialId = socialId;
    }
}