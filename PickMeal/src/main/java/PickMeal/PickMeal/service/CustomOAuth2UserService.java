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
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuth2Attributes attributes = OAuth2Attributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // [수정] 네이버를 포함하여 각 플랫폼에 맞는 정확한 DB ID 생성
        String socialId = "";
        if ("naver".equals(registrationId)) {
            // 네이버는 attributes 안에 'response' 맵이 있고 그 안에 'id'가 있습니다.
            Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes().get("response");
            socialId = String.valueOf(response.get("id"));
        } else if ("kakao".equals(registrationId)) {
            socialId = String.valueOf(oAuth2User.getAttributes().get("id"));
        } else {
            socialId = String.valueOf(oAuth2User.getAttributes().get("sub")); // 구글
        }

        String userId = registrationId + "_" + socialId;
        User user = userMapper.findById(userId);

        if (user == null && attributes.getEmail() != null) {
            user = userMapper.findByEmail(attributes.getEmail());
        }

        // [수정] 에러 메시지를 명확하게 전달
        if (user == null) {
            user = User.builder()
                    .id(userId)
                    .password("SOCIAL_LOGIN")
                    .nickname(attributes.getName()) // 소셜에서 제공하는 기본 닉네임(혹은 이름)
                    .name(attributes.getName())
                    .email(attributes.getEmail())
                    .role("ROLE_USER")
                    .status("ACTIVE")
                    .build();
            userMapper.insertUser(user);
        } else {

        }

        // 2. [기존 기능 유지] 탈퇴한 회원인지 체크
        if (user != null && "WITHDRAWN".equals(user.getStatus())) {
            OAuth2Error oauth2Error = new OAuth2Error("withdrawn_user", "탈퇴한 회원입니다.", null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }

        // 3. [핵심 수정] 세션 속성(Attributes)에 DB의 닉네임을 강제로 주입
        // oAuth2User.getAttributes()는 읽기 전용일 수 있으므로 새로운 Map을 생성합니다.
        Map<String, Object> customAttributes = new HashMap<>(oAuth2User.getAttributes());

        // 시큐리티 세션 내부에 "nickname"이라는 키로 실제 닉네임을 저장합니다.
        customAttributes.put("nickname", user.getNickname());
        // 또한 플랫폼 기본 Name 키값 위치에도 닉네임을 덮어씌워 숫자 ID 대신 뜨게 만듭니다.
        customAttributes.put(userNameAttributeName, user.getNickname());

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole())), // ROLE_USER 대신 유저 객체의 권한 사용
                customAttributes,
                userNameAttributeName
        );
    }
}