package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.domain.User;
import PickMeal.PickMeal.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new User()); // 일반 가입 시 빈 유저 객체 바인딩
        model.addAttribute("isSocial", false); // 프론트에서 소셜 유저임을 판별
        return "users/signupForm";
    }

    @PostMapping("/signup")
    public String signUp(User user, RedirectAttributes rttr){
        try {
            userService.signUp(user); // 가입 로직 호출
            rttr.addFlashAttribute("msg", "회원가입이 완료되었습니다.");
            return "redirect:/users/login";
        } catch (IllegalStateException e){
            rttr.addFlashAttribute("msg", e.getMessage()); // 중복 가입 에러 메시지 전달
            return "redirect:/users/signup";
        }
    }

    @GetMapping("/check-id")
    @ResponseBody // JSON/문자열 데이터만 반환
    public String checkId(@RequestParam("id") String id) {
        if (id == null || id.trim().isEmpty()) return "invalid";
        return userService.isIdDuplicate(id) ? "fail" : "success"; // 중복 검사 결과 반환
    }

    @GetMapping("/signup/social")
    public String socialSignupForm(@RequestParam String socialId, @RequestParam String email, @RequestParam String site, Model model) {
        User socialUser = new User(); // 소셜 전용 유저 객체 생성
        socialUser.setSocialId(socialId);
        socialUser.setSocialLoginSite(site);
        socialUser.setEmail(email);
        socialUser.setId(site + "_" + socialId); // 예: google_12345
        model.addAttribute("user", socialUser); // 폼에 미리 채워진 데이터 전달
        model.addAttribute("isSocial", true); // 프론트에서 소셜 유저임을 판별
        return "users/signupForm";
    }

    @GetMapping("/login")
    public String loginForm(HttpSession session, Model model) {
        String errorMessage = (String) session.getAttribute("errorMessage"); // 로그인 실패 메시지
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            session.removeAttribute("errorMessage"); // 노출 후 세션 제거
        }
        return "users/loginForm";
    }

    @PostMapping("/remove") // 회원 탈퇴 요청
    public String remove(@AuthenticationPrincipal User user) {
        userService.remove(user.getUser_id());
        return "redirect:/";
    }

    @GetMapping("/edit") // 수정 폼 호출
    public String editForm(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "users/editForm";
    }

    @PostMapping("/edit") // 수정 실행
    public String edit(User user, RedirectAttributes rttr) {
        userService.edit(user);
        rttr.addFlashAttribute("msg", "정보 수정이 완료되었습니다.");
        return "redirect:/";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // yyyy-MM-dd 형식을 Date 객체로 변환해주는 도구를 등록합니다.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false); // 엄격한 날짜 체크
        binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(dateFormat, true));
    }
}
