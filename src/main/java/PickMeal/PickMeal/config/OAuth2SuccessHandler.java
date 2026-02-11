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

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // OAuth2AuthenticationToken으로 캐스팅하면 정보를 더 많이 가져올 수 있습니다.
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;

        // 1. 여기서 google, kakao, naver 등의 값을 자동으로 가져옵니다.
        String registrationId = authToken.getAuthorizedClientRegistrationId();

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String socialId = oAuth2User.getName();
        String email = oAuth2User.getAttribute("email");

        // 2. 우리가 정한 규칙대로 ID 생성 (예: google_1234567)
        String userId = registrationId + "_" + socialId;

        User findUser = userService.findById(userId);

        if (findUser != null) {
            getRedirectStrategy().sendRedirect(request, response, "/");
        } else {
            // 신규 회원이면 어떤 사이트(site)인지 담아서 가입 폼으로 보냄
            String targetUrl = UriComponentsBuilder.fromUriString("/users/signup/social")
                    .queryParam("socialId", socialId)
                    .queryParam("email", email)
                    .queryParam("site", registrationId) // 동적으로 전달
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();

            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
    }
}