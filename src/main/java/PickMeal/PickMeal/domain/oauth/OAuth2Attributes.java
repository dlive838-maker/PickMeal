package PickMeal.PickMeal.domain.oauth;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class OAuth2Attributes { // 소셜별로 제각각인 응답 데이터를 우리 서비스 규격으로 통일하는 DTO입니다.
    private Map<String, Object> attributes; // 소셜 서버에서 받은 전체 원본 데이터
    private String nameAttributeKey; // OAuth2 로그인 시 키가 되는 필드값 (PK 역할)
    private String name; // 사용자 이름 (또는 닉네임)
    private String email; // 사용자 이메일

    @Builder
    public OAuth2Attributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
    }

    /**
     * registrationId(google, kakao, naver)를 구분하여 각 서비스에 맞는 추출 메서드를 호출합니다.
     */
    public static OAuth2Attributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("naver".equals(registrationId)) {
            return ofNaver("id", attributes); // 네이버 로그인 처리 호출
        }
        if ("kakao".equals(registrationId)) {
            return ofKakao("id", attributes); // 카카오 로그인 처리 호출
        }
        return ofGoogle(userNameAttributeName, attributes); // 기본값으로 구글 처리 호출
    }

    /**
     * 구글에서 받은 정보를 매핑합니다.
     */
    private static OAuth2Attributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .name((String) attributes.get("name")) // 구글은 'name' 키를 사용합니다.
                .email((String) attributes.get("email")) // 구글은 'email' 키를 사용합니다.
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    /**
     * 네이버에서 받은 정보를 매핑합니다.
     */
    private static OAuth2Attributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        // 네이버는 유저 정보가 'response'라는 Map 안에 담겨서 옵니다.
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2Attributes.builder()
                .name((String) response.get("name")) // 네이버 응답 내 'name' 추출
                .email((String) response.get("email")) // 네이버 응답 내 'email' 추출
                .attributes(response) // 세션에는 response 안의 알맹이 데이터만 저장하도록 설정
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    /**
     * 카카오에서 받은 정보를 매핑합니다.
     */
    private static OAuth2Attributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        // 카카오는 'kakao_account' 안에 'profile'과 'email'이 나누어져 있습니다.
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuth2Attributes.builder()
                .name((String) profile.get("nickname")) // 카카오 프로필의 닉네임 추출
                .email((String) kakaoAccount.get("email")) // 카카오 계정의 이메일 추출
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
}