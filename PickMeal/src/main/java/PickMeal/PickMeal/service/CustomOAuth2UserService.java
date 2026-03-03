package PickMeal.PickMeal.service;

import PickMeal.PickMeal.domain.User;
import PickMeal.PickMeal.domain.oauth.OAuth2Attributes;
import PickMeal.PickMeal.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service // 소셜 로그인 데이터 처리 서비스
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserMapper userMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> originAttributes = oAuth2User.getAttributes();

        // 1. 원본에서 ID 추출
        String rawId = "";
        if ("naver".equals(registrationId)) {
            Map<String, Object> response = (Map<String, Object>) originAttributes.get("response");
            rawId = String.valueOf(response.get("id"));
        } else if ("kakao".equals(registrationId)) {
            rawId = String.valueOf(originAttributes.get("id"));
        } else {
            rawId = String.valueOf(originAttributes.get("sub"));
        }

        // 2. [필살기] 혹시라도 이미 붙어있을지 모르는 모든 접두사 제거 (중복 방지)
        // "kakao_kakao_4746" -> "4746" 으로 강제 세척
        String cleanId = rawId.replace(registrationId + "_", "").replace(registrationId, "");

        // 3. 딱 한 번만 깔끔하게 붙임 -> "kakao_4746951582"
        String finalId = registrationId + "_" + cleanId;

        System.out.println(">>> [최종 확정 ID] : " + finalId);

        // 4. 탈퇴 체크
        User user = userMapper.findById(finalId);
        if (user != null && "WITHDRAWN".equals(user.getStatus())) {
            throw new OAuth2AuthenticationException(new OAuth2Error("withdrawn_user"), "탈퇴 회원");
        }

        // 5. 세션 설정 (db_id라는 키로 저장)
        Map<String, Object> customMap = new java.util.HashMap<>(originAttributes);
        customMap.put("db_id", finalId);

        return new DefaultOAuth2User(
                Collections.singleton(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")),
                customMap,
                "db_id" // 식별자 키 고정
        );
    }
}