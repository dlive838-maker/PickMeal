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

        // 1. 로그인 성공 정보를 OAuth2 전용 토큰 형태로 변환하여 세부 정보를 꺼냅니다.
        // OAuth2 전용 토큰 : 소셜 서버로부터 전달받은 유저의 모든 정보가 담긴 '공식 인증서 객체'
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;

        // 2. 어떤 소셜 서비스(google, kakao, naver 등)를 통해 들어왔는지 확인합니다.
        String registrationId = authToken.getAuthorizedClientRegistrationId();

        // 3. 소셜 서버에서 넘겨준 사용자의 상세 속성값(이메일, 이름 등)을 가져옵니다.
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = "";
        String name = "";
        String socialId = ""; // 각 소셜 서비스가 부여한 사용자의 고유 식별 번호를 담을 변수입니다.

        // 4. 소셜 서비스마다 데이터 보따리를 푸는 방식이 다르므로 서비스별로 분기 처리합니다.
        if ("kakao".equals(registrationId)) {
            socialId = oAuth2User.getName(); // 카카오는 최상위에 id가 있어 바로 꺼냅니다.
            Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
            if (kakaoAccount != null) {
                email = (String) kakaoAccount.get("email"); // 카카오 계정 내 이메일 추출
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                if (profile != null) name = (String) profile.get("nickname"); // 프로필 내 닉네임 추출
            }
        } else if ("naver".equals(registrationId)) {
            // 네이버는 모든 정보가 'response'라는 이름의 주머니(Map) 안에 들어있습니다.
            Map<String, Object> naverResponse = oAuth2User.getAttribute("response");
            if (naverResponse != null) {
                socialId = (String) naverResponse.get("id"); // 주머니 안에서 진짜 고유 ID만 쏙 골라냅니다.
                email = (String) naverResponse.get("email"); // 주머니 내 이메일 추출
                name = (String) naverResponse.get("name"); // 주머니 내 이름 추출
            }

        } else { // Google 등 기타 서비스 처리
            socialId = oAuth2User.getName(); // 구글은 getName()이 고유 ID를 반환합니다.
            email = oAuth2User.getAttribute("email");
            name = oAuth2User.getAttribute("name");
        }

        // 5. 우리 DB에 저장할 '고유 아이디'를 만듭니다 (예: naver_12345). 중복 방지를 위해 소셜사 이름을 붙입니다.
        String userId = registrationId + "_" + socialId;

        // 6. 생성된 아이디로 우리 DB에 이미 가입된 회원인지 확인합니다.
        User findUser = userService.findById(userId);

        if (findUser != null) {
            // [중요 수정] 정식 회원이라면 시큐리티 세션에 우리 도메인 User 정보를 심어줍니다.
            // 이 과정이 있어야 컨트롤러에서 @AuthenticationPrincipal User로 정보를 받을 수 있습니다.

            // 7-1. 이미 회원이라면 메인 페이지로 보냅니다.
            getRedirectStrategy().sendRedirect(request, response, "/next-page");
        } else {
            // 7-2. 처음 방문한 사람이라면 추가 정보를 받기 위해 회원가입 페이지로 정보를 들고 이동시킵니다.
            String targetUrl = UriComponentsBuilder.fromUriString("/users/signup/social")
                    .queryParam("socialId", socialId) // 소셜 고유 ID를 파라미터로 전달
                    .queryParam("email", email)      // 가져온 이메일을 가입 폼에 미리 채워주기 위해 전달
                    .queryParam("name", name)        // 가져온 이름을 가입 폼에 미리 채워주기 위해 전달
                    .queryParam("site", registrationId) // 어느 소셜 출신인지 전달
                    .build()
                    .encode(StandardCharsets.UTF_8) // 한글 이름이 깨지지 않도록 UTF-8로 인코딩합니다.
                    .toUriString();

            getRedirectStrategy().sendRedirect(request, response, targetUrl); // 만들어진 주소로 강제 이동(리다이렉트)시킵니다.
        }
    }
}