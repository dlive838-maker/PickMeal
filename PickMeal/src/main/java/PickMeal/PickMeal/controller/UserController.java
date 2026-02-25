package PickMeal.PickMeal.controller;

import PickMeal.PickMeal.domain.User;
import PickMeal.PickMeal.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
import java.util.Map;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * [핵심] 소셜/일반 로그인 통합 ID 추출 메서드
     * 네이버, 카카오, 구글의 각기 다른 데이터 구조를 분석하여 DB용 ID를 반환합니다.
     */
    private String getLoginUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;

        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String registrationId = oauthToken.getAuthorizedClientRegistrationId(); // google, kakao, naver
            OAuth2User oAuth2User = oauthToken.getPrincipal();
            Map<String, Object> attributes = oAuth2User.getAttributes();
            String socialId = "";

            try {
                if ("naver".equals(registrationId)) {
                    Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                    socialId = String.valueOf(response.get("id"));
                } else if ("kakao".equals(registrationId)) {
                    // 카카오는 id가 Long이거나 Integer일 수 있으므로 확실하게 처리
                    Object id = attributes.get("id");
                    socialId = (id != null) ? id.toString() : "";
                } else if ("google".equals(registrationId)) {
                    // 구글은 sub가 핵심 ID입니다.
                    Object sub = attributes.get("sub");
                    socialId = (sub != null) ? sub.toString() : "";
                }

                // 추출한 ID가 비어있다면 최후의 수단으로 getName() 사용
                if (socialId.isEmpty() || "null".equals(socialId)) {
                    socialId = oAuth2User.getName();
                }
            } catch (Exception e) {
                // 에러 발생 시 로그를 남기고 getName()으로 방어
                System.out.println("소셜 ID 추출 중 오류 발생: " + e.getMessage());
                socialId = oAuth2User.getName();
            }

            return registrationId + "_" + socialId;
        }

        return authentication.getName(); // 일반 로그인
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // yyyy-MM-dd 형식을 Date 객체로 변환해주는 도구를 등록합니다.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false); // 엄격한 날짜 체크
        binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(dateFormat, true));
    }

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

    @GetMapping("/check-nickname")
    @ResponseBody
    public boolean checkNicknameDuplication(@RequestParam("nickname") String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            return false; // 빈 값은 사용 불가로 처리
        }
        // 서비스에서 중복이면 true, 사용 가능하면 false 반환
        boolean isExist = userService.existsByNickname(nickname);

        // JS에서 사용 가능 여부로 판단하기 쉽게 반전해서 보낼 수도 있지만,
        // 여기서는 '중복 여부' 자체를 보내고 JS에서 처리하는 게 깔끔합니다.
        // 중복이면 1을 보내고, 사용 가능하면 0을 보냅니다.
        return isExist; //
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

    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "error", required = false) String error,
                            HttpSession session, Model model) {

        // 일반 로그인 실패 시 시큐리티가 세션에 담아둔 에러 메시지 확인
        String errorMessage = (String) session.getAttribute("errorMessage");
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            session.removeAttribute("errorMessage");
        }

        // [추가] 소셜 로그인 실패(탈퇴 회원 등) 시 URL 파라미터로 넘어오는 에러 처리
        // 시큐리티 설정에 따라 에러가 발생하면 /login?error 형태로 이동합니다.
        if (error != null) {
            model.addAttribute("errorMessage", "탈퇴 처리된 계정입니다. 고객센터에 문의해주세요.");
        }

        return "users/login";
    }

    @GetMapping("/mypage")
    public String mypage(Authentication authentication, Model model) {
        String userId = getLoginUserId(authentication);
        if (userId == null) return "redirect:/users/login";

        User latestUserInfo = userService.findById(userId);
        if (latestUserInfo == null) return "redirect:/users/login";

        // [리팩토링] 서비스의 공통 메서드 호출
        String displayId = userService.getMaskedDisplayId(latestUserInfo);

        model.addAttribute("user", latestUserInfo);
        model.addAttribute("displayId", displayId);
        return "users/mypage";
    }

    @GetMapping("/edit")
    public String editForm(Authentication authentication, Model model) {
        String userId = getLoginUserId(authentication);
        if (userId == null) return "redirect:/users/login";

        User user = userService.findById(userId);
        if (user == null) return "redirect:/users/login";

        // [리팩토링] 서비스의 공통 메서드 호출
        String displayId = userService.getMaskedDisplayId(user);

        model.addAttribute("user", user);
        model.addAttribute("displayId", displayId);
        return "users/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute User user, RedirectAttributes rttr, Authentication authentication) {
        // [중요] 폼의 hidden id 대신 인증 세션의 ID를 사용하여 보안 강화
        String userId = getLoginUserId(authentication);
        User existingUser = userService.findById(userId);

        if (existingUser == null) {
            rttr.addFlashAttribute("msg", "사용자 정보를 찾을 수 없습니다.");
            return "redirect:/users/mypage";
        }

        // DB 원본 데이터 병합 (마스킹 데이터 방어)
        user.setPassword(existingUser.getPassword());
        user.setEmail(existingUser.getEmail());
        user.setName(existingUser.getName());
        user.setPhoneNumber(existingUser.getPhoneNumber());
        user.setSocialLoginSite(existingUser.getSocialLoginSite());
        user.setSocialId(existingUser.getSocialId());
        user.setRole(existingUser.getRole());
        user.setStatus(existingUser.getStatus());

        userService.edit(user, false);
        rttr.addFlashAttribute("msg", "정보 수정이 완료되었습니다.");
        return "redirect:/users/mypage";
    }

    /**
     * 팝업창 이메일 즉시 변경 API
     * @RequestBody를 사용하여 JSON 데이터를 User 객체로 매핑합니다.
     */
    @PostMapping("/update-email")
    @ResponseBody
    public ResponseEntity<String> updateEmail(@RequestBody User userRequest, Authentication authentication) {
        try {
            String userId = getLoginUserId(authentication);
            User loginUser = userService.findById(userId);

            if (loginUser == null) return ResponseEntity.status(401).body("unauthorized");

            // [추가] 소셜 로그인 사용자인지 확인하여 에러 반환
            if (loginUser.getSocialLoginSite() != null && !loginUser.getSocialLoginSite().isEmpty()) {
                return ResponseEntity.status(403).body("social_user_cannot_change_email");
            }

            userService.updateEmail(loginUser.getUser_id(), userRequest.getEmail());
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("fail");
        }
    }

    /**
     * 팝업창 비밀번호 즉시 변경 API
     */
    @PostMapping("/update-password")
    @ResponseBody
    public String updatePassword(@RequestBody java.util.Map<String, String> params,
                                 Authentication authentication) {
        String currentPassword = params.get("currentPassword");
        String newPassword = params.get("newPassword");

        // [수정] 직접 캐스팅 대신 공통 메서드로 ID를 찾아 DB에서 유저를 새로 조회합니다.
        String userId = getLoginUserId(authentication);
        User user = userService.findById(userId);

        if (user == null) return "fail";

        // 소셜 사용자가 비밀번호가 없는 경우에 대한 예외 처리
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return "social_user_cannot_change_pw";
        }

        // [체크 포인트] matches 메서드에 들어가는 인자가 정확히 String인지 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return "current_password_incorrect";
        }

        try {
            userService.updatePassword(user.getUser_id(), newPassword);
            return "success";
        } catch (Exception e) {
            return "fail";
        }
    }

    @PostMapping("/remove")
    public String remove(Authentication authentication, HttpSession session, RedirectAttributes rttr) {
        // 1. 인증 객체 체크
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/users/login";
        }

        try {
            // [핵심 수정] 우리가 만든 공통 메서드로 실제 DB용 ID를 가져옵니다.
            String userId = getLoginUserId(authentication);
            User user = userService.findById(userId);

            if (user != null) {
                // 2. 서비스 호출: status를 'WITHDRAWN'으로 변경 (Soft Delete)
                userService.remove(user.getUser_id());

                // 3. 세션 무효화 및 시큐리티 컨텍스트 클리어 (로그아웃 처리)
                session.invalidate();
                // 시큐리티 권한 정보도 완전히 삭제하는 것이 안전합니다.
                org.springframework.security.core.context.SecurityContextHolder.clearContext();

                rttr.addFlashAttribute("msg", "회원 탈퇴가 정상적으로 처리되었습니다.");
                return "redirect:/?msg=withdrawn";
            } else {
                rttr.addFlashAttribute("msg", "유저 정보를 찾을 수 없어 탈퇴에 실패했습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            rttr.addFlashAttribute("msg", "탈퇴 처리 중 오류가 발생했습니다.");
        }

        return "redirect:/users/mypage";
    }

}