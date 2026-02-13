package PickMeal.PickMeal.service;

import PickMeal.PickMeal.domain.User;
import PickMeal.PickMeal.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // 서비스 빈 등록
@RequiredArgsConstructor // final 필드 생성자 주입
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 적용
public class UserService implements UserDetailsService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional // 쓰기 작업이므로 트랜잭션 별도 지정
    public void signUp(User user) {
        validateDuplicateUser(user.getId()); // 중복 가입 체크

        // 소셜 가입자인데 비밀번호가 없는 경우 시스템용 임시 비밀번호 생성
        if (user.getSocialId() != null && (user.getPassword() == null || user.getPassword().isEmpty())) {
            String tempPassword = java.util.UUID.randomUUID().toString();
            user.setPassword(passwordEncoder.encode(tempPassword));
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword())); // 일반 유저 비번 암호화
        }

        user.setRole("ROLE_USER"); // 기본 권한 설정
        user.setStatus("ACTIVE"); // 계정 활성화 상태 설정
        userMapper.save(user); // DB 저장
    }

    // 아이디 중복체크
    public boolean isIdDuplicate(String id) {
        return userMapper.findById(id) != null; // 존재 여부 반환
    }

    /**
     * 닉네임 중복 확인
     *
     * @param nickname 검사할 닉네임
     * @return 중복이면 true, 사용 가능하면 false
     */
    public boolean existsByNickname(String nickname) {
        int count = userMapper.countByNickname(nickname);
        return count > 0; // 0보다 크면 이미 존재하는 닉네임입니다.
    }


    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        User user = userMapper.findById(id); // 시큐리티 로그인 시 아이디로 조회
        if (user == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + id);
        }
        return user; // UserDetails 구현체인 User 반환
    }

    private void validateDuplicateUser(String id) {
        if (userMapper.findById(id) != null) {
            throw new IllegalStateException("이미 존재하는 아이디입니다."); // 예외 발생
        }
    }

    public User findById(String id) {
        return userMapper.findById(id); // 아이디로 유저 검색
    }

    @Transactional
    public void edit(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword())); // 비번 변경 시 암호화
        }
        userMapper.edit(user); // 업데이트 실행
    }

    @Transactional
    public void remove(Long user_id) {
        userMapper.updateStatus(user_id, "WITHDRAWN"); // 탈퇴 시 상태값만 변경
    }
}
