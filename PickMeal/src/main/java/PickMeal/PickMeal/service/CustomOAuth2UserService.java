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

        // [수정] 에러 메시지를 명확하게 전달
        if (user != null && "WITHDRAWN".equals(user.getStatus())) {
            // 에러 코드를 "withdrawn_user"로 지정하여 던집니다.
            OAuth2Error oauth2Error = new OAuth2Error("withdrawn_user", "탈퇴한 회원입니다.", null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }
}