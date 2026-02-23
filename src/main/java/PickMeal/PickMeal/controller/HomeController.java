package PickMeal.PickMeal.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Map;
import PickMeal.PickMeal.entity.UserEntity;
import PickMeal.PickMeal.repository.UserRepository;

@Controller // 홈페이지 전용 주방장.
public class HomeController {

    // 1. 전용 볼펜(Repository)을 주방장에게 쥐어준다.
    private final UserRepository userRepository;

    // 2. 생성자를 통해 볼펜을 전달받는다. (이게 있어야 DB를 쓸 수 있다!)
    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/") // 홈페이지 주소인 '/' 요청을 처리한다.
    public String home(Model model, @AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            // 네이버 신분증에서 이름을 꺼내 쟁반에 담는다.
            Map<String, Object> attributes = (Map<String, Object>) principal.getAttribute("response");
            if (attributes != null) {
                // 3. 네이버에서 이메일과 이름을 꺼낸다.
                String email = (String) attributes.get("email");
                String name = (String) attributes.get("name");
                String nickname = (String) attributes.get("nickname"); // 추가!
                String gender = (String) attributes.get("gender");
                String phoneNumber = (String) attributes.get("mobile");
                String birthYear = (String) attributes.get("birthyear"); // 👈 네이버에서 '연도' 꺼내기 (예: 1993)
                String birthDay = (String) attributes.get("birthday");   // 👈 네이버에서 '월-일' 꺼내기 (예: 10-07)
                // 두 재료를 대시(-)로 이어붙여서 하나의 생일 문장을 만든다.
                String birthDate = birthYear + "-" + birthDay;           // 결과: "1993-10-07"
                String socialId = (String) attributes.get("id"); // 네이버의 고유 ID('id')를 꺼낸다.

                // 4. [핵심] 장부에 이 이메일이 없으면 새로 저장(회원가입)한다!
                UserEntity user = userRepository.findByEmail(email)
                        .orElseGet(() -> {
                            System.out.println("신규 회원입니다! DB에 저장합니다.");
                            return userRepository.save(new UserEntity(email, name, "naver", nickname, gender, phoneNumber, birthDate, socialId));
                        });
                // DB에서 가져온 user 객체로부터 별명을 꺼내 "userNickname"이라는 이름으로 쟁반에 담는다.
                model.addAttribute("userNickname", user.getNickname());
                model.addAttribute("userName", attributes.get("name"));
                // 장부(user)에서 취향 정보를 꺼내 쟁반(model)에 담는다.
                model.addAttribute("userLikeMenu", user.getLikeMenu());
                model.addAttribute("userDisLikeMenu", user.getDisLikeMenu());
            }
        }
        return "index"; // templates/index.html 파일을 보여준다.
    }

    @PostMapping("/update-menu") // HTML의 폼 데이터를 이 주소로 받습니다.
    public String updateMenu(@AuthenticationPrincipal OAuth2User principal,
                             String likeMenu,
                             String disLikeMenu) {

        if (principal != null) {
            // 현재 로그인 중인 손님의 이메일을 확인합니다.
            Map<String, Object> attributes = (Map<String, Object>) principal.getAttribute("response");
            String email = (String) attributes.get("email");

            // 장부(Repository)에서 손님을 찾아 @Setter로 내용을 수정합니다.
            userRepository.findByEmail(email).ifPresent(user -> {
                user.setLikeMenu(likeMenu);       // 좋아하는 음식 업데이트
                user.setDisLikeMenu(disLikeMenu); // 싫어하는 음식 업데이트
                userRepository.save(user);        // 변경 내용을 DB에 최종 저장
            });
        }
        return "redirect:/"; // 작업 후 다시 홈으로 돌아갑니다.
    }

}