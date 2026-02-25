package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.domain.User;
import PickMeal.PickMeal.service.EmailService;
import PickMeal.PickMeal.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final UserService userService; // [추가] 유저 존재 여부 확인을 위해 주입

    // 1. 인증 메일 발송 요청 처리
    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestParam("email") String email, HttpSession session) {
        User existingUser = userService.findByEmail(email);

        if (existingUser != null) {
            // 소셜 가입자인 경우 (socialLoginSite 컬럼에 값이 있는 경우)
            if (existingUser.getSocialLoginSite() != null && !existingUser.getSocialLoginSite().isEmpty()) {
                // "social_kakao" 또는 "social_google" 형식으로 응답
                return ResponseEntity.status(409).body("social_" + existingUser.getSocialLoginSite());
            }
            // 일반 가입자인 경우
            return ResponseEntity.status(409).body("basic");
        }

        try {
            String code = emailService.createCode();
            session.setAttribute("emailCode", code);
            session.setAttribute("sendTime", System.currentTimeMillis());
            emailService.sendMail(email, code);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("error");
        }
    }
    // 2. 인증번호 확인 로직 (기존과 동일하되 반환 타입만 통일)
    @PostMapping("/verify")
    public String verifyCode(@RequestParam("code") String code, HttpSession session) {
        String savedCode = (String) session.getAttribute("emailCode");
        Long sendTime = (Long) session.getAttribute("sendTime");

        if (savedCode == null || sendTime == null) return "expired";

        long currentTime = System.currentTimeMillis();
        if (currentTime - sendTime > 5 * 60 * 1000) {
            session.removeAttribute("emailCode");
            session.removeAttribute("sendTime");
            return "timeout";
        }

        if (savedCode.equals(code)) {
            session.removeAttribute("emailCode");
            session.removeAttribute("sendTime");
            return "success";
        }
        return "fail";
    }
}