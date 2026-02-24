package PickMeal.PickMeal.service;

import PickMeal.PickMeal.domain.Food;
import PickMeal.PickMeal.domain.User;
import PickMeal.PickMeal.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    /**
     * 사용자 아이디 마스킹 공통 로직
     * @param user 마스킹할 유저 객체
     * @return 마스킹 처리된 디스플레이용 아이디
     */
    /**
     * 사용자 아이디 마스킹 공통 로직
     * 구글(sub), 카카오(id), 네이버(id)의 모든 케이스를 대응합니다.
     */
    public String getMaskedDisplayId(User user) {
        if (user == null) return "Unknown User";

        // 타겟 ID 설정
        String targetId = (user.getSocialLoginSite() != null && !user.getSocialLoginSite().isEmpty())
                ? user.getSocialId() : user.getId();

        // 만약 targetId가 null이면 DB의 id를 차선책으로 사용
        if (targetId == null || targetId.isEmpty()) {
            targetId = user.getId();
        }

        // 최종적으로도 null이면 반환
        if (targetId == null || targetId.isEmpty()) return "Unknown";

        String displayId = "";
        int len = targetId.length();

        // 마스킹 규칙 적용
        if (len <= 3) {
            displayId = targetId.substring(0, Math.min(len, 1)) + "******";
        } else {
            displayId = targetId.substring(0, 3) + "******";
        }

        // 소셜 접두사 추가
        if (user.getSocialLoginSite() != null && !user.getSocialLoginSite().isEmpty()) {
            displayId = user.getSocialLoginSite().toUpperCase() + "_" + displayId;
        }

        return displayId;
    }

    @Transactional
    public void edit(User user, boolean isNewPassword) {
        if (isNewPassword) {
            // 새 비밀번호가 입력된 경우에만 암호화 수행
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        // 이미 암호화된 기존 비번일 경우(isNewPassword=false) 그대로 DB 업데이트
        userMapper.edit(user);
    }

    @Transactional
    public void remove(Long user_id) {
        userMapper.updateStatus(user_id, "WITHDRAWN"); // 탈퇴 시 상태값만 변경
    }

    public boolean isWithdrawn(User user) {
        return user != null && "WITHDRAWN".equals(user.getStatus());
    }

    public List<Food> getMixedFoods(List<String> types, int round) {
        return userMapper.getMixedFoods(types, round);
    }

    // 랭킹 페이지용 데이터 가져오기
    public List<Food> getTop10Foods() {
        return userMapper.getTop10Foods();
    }

    @Transactional
    public void updateFoodWinCount(Long foodId) {
        // [비유] 일꾼이 실제 장부(Mapper)를 들고 가서 숫자를 하나 올립니다.
        userMapper.updateWinCount(foodId);
    }

    /**
     * 이메일 즉시 변경
     */
    @Transactional
    public void updateEmail(Long userId, String newEmail) {
        // MyBatis 매퍼를 통해 특정 컬럼만 업데이트하는 전용 메서드를 호출하거나,
        // 기존 edit을 재사용할 수 있도록 로직을 짭니다.
        User user = new User();
        user.setUser_id(userId);
        user.setEmail(newEmail);
        userMapper.updateEmail(user); // 매퍼에 새 메서드 추가 필요
    }

    /**
     * 비밀번호 즉시 변경
     */
    @Transactional
    public void updatePassword(Long userId, String rawPassword) {
        User user = new User();
        user.setUser_id(userId);
        user.setPassword(passwordEncoder.encode(rawPassword)); // 암호화 필수
        userMapper.updatePassword(user); // 매퍼에 새 메서드 추가 필요
    }
}
