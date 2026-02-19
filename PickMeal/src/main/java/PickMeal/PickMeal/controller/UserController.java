package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.domain.User;
import PickMeal.PickMeal.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
        return "users/signup";
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

    @GetMapping("/check-nickname") // 중복 체크를 위한 별도 경로
    @ResponseBody // 페이지가 아닌 '데이터(문자열)'만 응답하기 위해 사용
    public String checkNicknameDuplication(@RequestParam("nickname") String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            return "invalid";
        }
        // 1. 서비스에 닉네임 중복 확인 요청
        boolean isExist = userService.existsByNickname(nickname);

        // 2. 결과에 따라 성공/실패 메시지 반환
        return isExist ? "fail" : "success";
    }

    @GetMapping("/signup/social")
    public String socialSignupForm(@RequestParam String socialId, @RequestParam String email, @RequestParam String site, @RequestParam String name, Model model) {
        User socialUser = new User(); // 소셜 전용 유저 객체 생성
        socialUser.setSocialId(socialId);
        socialUser.setSocialLoginSite(site);
        socialUser.setNickname(name);
        socialUser.setEmail(email);
        socialUser.setId(site + "_" + socialId); // 예: google_12345
        model.addAttribute("user", socialUser); // 폼에 미리 채워진 데이터 전달
        model.addAttribute("isSocial", true); // 프론트에서 소셜 유저임을 판별
        return "users/signup";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // yyyy-MM-dd 형식을 Date 객체로 변환해주는 도구를 등록합니다.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false); // 엄격한 날짜 체크
        binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(dateFormat, true));
    }

    @GetMapping("/login")
    public String loginForm(HttpSession session, Model model) {
        // 세션에서 에러 메시지 꺼내기
        String errorMessage = (String) session.getAttribute("errorMessage");

        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage); // 화면으로 전달
            session.removeAttribute("errorMessage"); // 1회성 노출 후 제거
        }
        return "users/login";
    }

    @GetMapping("/mypage")
    public String mypage(Authentication authentication, Model model) {
        // 1. 인증 객체 체크 (로그인 여부 확인)
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/users/login";
        }

        String userId = "";

        // 2. 인증 객체 타입에 따라 DB 조회용 ID 추출
        if (authentication instanceof OAuth2AuthenticationToken) {
            // 소셜 로그인인 경우
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String registrationId = oauthToken.getAuthorizedClientRegistrationId();
            OAuth2User oAuth2User = oauthToken.getPrincipal();

            String socialId = "";
            if ("naver".equals(registrationId)) {
                java.util.Map<String, Object> response = (java.util.Map<String, Object>) oAuth2User.getAttribute("response");
                socialId = (String) response.get("id");
            } else {
                socialId = oAuth2User.getName(); // 카카오, 구글 등
            }
            userId = registrationId + "_" + socialId;
        } else {
            // 일반 로그인인 경우 (시큐리티 세션에 저장된 id 사용)
            userId = authentication.getName();
        }

        // 3. DB 조회 (최신 사용자 정보 가져오기)
        User latestUserInfo = userService.findById(userId);

        if (latestUserInfo == null) {
            System.out.println("조회된 유저가 없습니다. ID: " + userId);
            return "redirect:/users/login";
        }

        // 4. 아이디 길이에 대응하는 마스킹 및 디스플레이 로직
        String displayId = "";

        // 소셜 로그인 사이트 정보가 있는 경우 (소셜 회원)
        if (latestUserInfo.getSocialLoginSite() != null && !latestUserInfo.getSocialLoginSite().isEmpty()) {
            displayId = latestUserInfo.getSocialLoginSite().toUpperCase();
        }
        // 일반 회원인 경우 (아이디 마스킹 처리)
        else {
            String realId = latestUserInfo.getId(); // DB의 실제 아이디 사용
            int len = (realId != null) ? realId.length() : 0;

            if (len == 0) {
                displayId = "Unknown";
            } else if (len <= 3) {
                // 아이디가 3자 이하로 매우 짧은 경우: 첫 글자만 노출
                displayId = realId.substring(0, 1) + "*".repeat(len - 1);
            } else if (len <= 6) {
                // 아이디가 4~6자인 경우: 앞 2글자만 노출
                displayId = realId.substring(0, 2) + "*".repeat(len - 2);
            } else {
                // 아이디가 7자 이상인 경우: 앞 3글자만 노출
                displayId = realId.substring(0, 3) + "*".repeat(len - 3);
            }
        }

        model.addAttribute("user", latestUserInfo);
        model.addAttribute("displayId", displayId);
        return "users/mypage";
    }

    @GetMapping("/edit") // 수정 폼 호출
    public String editForm(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "users/edit";
    }

    @PostMapping("/edit") // 수정 실행
    public String edit(User user, RedirectAttributes rttr) {
        userService.edit(user);
        rttr.addFlashAttribute("msg", "정보 수정이 완료되었습니다.");
        return "redirect:/";
    }

    @PostMapping("/remove") // 회원 탈퇴 요청
    public String remove(@AuthenticationPrincipal User user) {
        userService.remove(user.getUser_id());
        return "redirect:/";
    }

}