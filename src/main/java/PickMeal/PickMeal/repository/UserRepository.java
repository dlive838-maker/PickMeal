package PickMeal.PickMeal.repository;

import PickMeal.PickMeal.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JpaRepository를 상속받으면 기본적인 '저장', '삭제', '조회' 기능이 자동으로 탑재.
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // 이메일로 이미 가입한 사람인지 확인하기 위한 '맞춤형 볼펜' 기능 추가
    // SQL: SELECT * FROM users WHERE email = ?
    Optional<UserEntity> findByEmail(String email);
}
