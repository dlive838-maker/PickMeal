package PickMeal.PickMeal.config;

import PickMeal.PickMeal.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

/**
 * Spring Security 설정 클래스
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationFailureHandler loginFailureHandler;
    // 수정: 작성하신 CustomOAuth2UserService를 주입받습니다.
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 해시 암호화 빈 등록
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, OAuth2SuccessHandler oauth2SuccessHandler) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/users/signup", "/users/signup/social", "/users/login", "/users/check-id", "/oauth2/**", "/css/**", "/js/**", "/images/**").permitAll() // 공개 경로
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )
                .formLogin(formLogin -> formLogin // 일반 로그인 설정
                        .loginPage("/users/login")
                        .usernameParameter("id")
                        .defaultSuccessUrl("/")
                        .failureHandler(loginFailureHandler)
                )
                .oauth2Login(oauth2 -> oauth2 // 소셜 로그인 설정
                        .loginPage("/users/login")
                        .successHandler(oauth2SuccessHandler)
                        // 추가: 유저 정보 추출 시 사용할 서비스를 등록합니다.
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                )
                .logout(logout -> logout // 로그아웃 설정
                        .logoutUrl("/users/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                );
        return http.build();
    }
}
