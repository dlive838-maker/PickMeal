package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.service.EmailService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController // 데이터를 주고받는 AJAX 통신용 컨트롤러 + ResponseBody 생략가능
@RequestMapping("/mail")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    // 1. 인증 메일 발송 요청 처리
    @PostMapping("/send")
    public String sendEmail(@RequestParam("email") String email, HttpSession session) {
        String code = emailService.createCode();

        // 1. 인증번호 저장
        session.setAttribute("emailCode", code);
        // 2. 현재 시간을 밀리초(ms) 단위로 저장
        session.setAttribute("sendTime", System.currentTimeMillis());

        emailService.sendMail(email, code);
        return "success";
    }

    @PostMapping("/verify")
    public String verifyCode(@RequestParam("code") String code, HttpSession session) {
        String savedCode = (String) session.getAttribute("emailCode");
        Long sendTime = (Long) session.getAttribute("sendTime");

        if (savedCode == null || sendTime == null) return "expired";

        // 현재 시간과 보낸 시간의 차이 계산 (5분 = 300,000ms)
        long currentTime = System.currentTimeMillis();
        if (currentTime - sendTime > 5 * 60 * 1000) {
            session.removeAttribute("emailCode");
            session.removeAttribute("sendTime");
            return "timeout"; // 5분 초과
        }

        if (savedCode.equals(code)) {
            session.removeAttribute("emailCode");
            session.removeAttribute("sendTime");
            return "success";
        }
        return "fail";
    }
}