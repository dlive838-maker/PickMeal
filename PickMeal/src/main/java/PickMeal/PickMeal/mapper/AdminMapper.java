package PickMeal.PickMeal.mapper;

import PickMeal.PickMeal.domain.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {
    // 로그인 아이디로 관리자 정보를 찾아옵니다.
    Admin findByLoginId(String loginId);

    // [추가] 관리자 성함으로 정보를 찾아옵니다.
    Admin findByName(String name);

    // 관리자 PK 번호로 정보를 찾아옵니다. (기록 저장 시 사용)
    Admin findById(Long adminId);
}