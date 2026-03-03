package PickMeal.PickMeal.config;

import PickMeal.PickMeal.domain.User;
import PickMeal.PickMeal.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 소셜 로그인 성공 후 처리를 담당하는 핸들러
 * 단순 로그인을 넘어 기존 회원 여부에 따라 경로를 배정해주는 '교통 정리' 역할을 함
 */
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService; // 우리 서비스의 DB와 통신하여 유저 정보를 조회하기 위한 서비스입니다.

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        // [핵심 수정] 서비스에서 이미 완벽하게 만들어둔 ID를 그대로 가져옵니다.
        // 더 이상 registrationId + "_" 같은 짓을 하지 않습니다.
        String userId = (String) oAuth2User.getAttributes().get("db_id");

        // 만약 db_id가 없으면 기본 name이라도 씁니다.
        if (userId == null) userId = oAuth2User.getName();

        System.out.println(">>> [핸들러 최종 검증] DB 조회할 ID: " + userId);

        User findUser = userService.findById(userId);

        if (findUser != null) {
            // 기존 회원: 메인으로 이동
            getRedirectStrategy().sendRedirect(request, response, "/next-page");
        } else {
            // 신규 회원: 가입 페이지로 이동 (기존 파라미터 로직 유지)
            // socialId 추출 시 이미 붙어있는 접두사를 제거해서 보냅니다 (가입폼 깔끔하게)
            String pureSocialId = userId.replace(registrationId + "_", "");

            Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttribute("kakao_account");
            String email = (kakaoAccount != null) ? (String) kakaoAccount.get("email") : "";

            String targetUrl = UriComponentsBuilder.fromUriString("/users/signup/social")
                    .queryParam("socialId", pureSocialId)
                    .queryParam("email", email)
                    .queryParam("site", registrationId)
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();

            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
    }
}