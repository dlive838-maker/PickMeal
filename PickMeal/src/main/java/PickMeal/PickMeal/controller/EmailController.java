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
    public ResponseEntity<String> sendEmail(
            @RequestParam("email") String email,
            @RequestParam(value = "type", defaultValue = "JOIN") String type, // [추가] 용도 파라미터
            HttpSession session) {

        // 프로필 수정(EDIT)일 때는 기존 이메일 중복 체크 로직을 건너뜁니다.
        if ("JOIN".equals(type)) {
            User existingUser = userService.findByEmail(email);
            if (existingUser != null) {
                return ResponseEntity.status(409).body("already_exists");
            }
        }

        try {
            String code = emailService.createCode();
            session.setAttribute("emailCode", code);
            session.setAttribute("sendTime", System.currentTimeMillis());

            // [수정] 서비스 호출 시 type도 함께 전달합니다.
            emailService.sendMail(email, code, type);
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