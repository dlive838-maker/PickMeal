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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationFailureHandler loginFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, OAuth2SuccessHandler oauth2SuccessHandler) throws Exception {
        http
                // 1. CSRF 예외 설정: POST 요청이 오는 경로들을 추가합니다.
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/mail/**",
                        "/users/find-id",
                        "/users/check-nickname",
                        "/users/find-password/**",
                        "/users/reset-password/**",
                        "/worldcup/win/**",
                        "/users/login",
                        "/api/wishlist/**",      // ★ 찜하기 POST 요청 허용
                        "/api/restaurant/**",    // ★ 조회수 증가 POST 요청 허용
                        "/api/review/**"         // ★ 리뷰 저장 POST 요청 허용
                ))
                // 2. 접근 권한 설정 (누구나 접근 가능한 페이지들)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/next-page", "/users/signup", "/users/signup/social", "/users/login",
                                "/users/check-id", "/users/check-nickname", "/users/find-id",
                                "/users/forgot-pw",         // 비번 찾기 신청 페이지
                                "/users/find-password/**",  // 비번 찾기 로직 API
                                "/users/reset-password/**", // 새 비번 설정 페이지 및 API
                                "/mail/**", "/oauth2/**", "/css/**", "/js/**", "/images/**", "/worldcup/win/**"
                                ).permitAll()
                        .requestMatchers("/meal-spotter/**", "/api/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/users/login")
                        .loginProcessingUrl("/users/login")
                        .usernameParameter("id")
                        .defaultSuccessUrl("/next-page", true)
                        .failureHandler(loginFailureHandler)
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/users/login")
                        .successHandler(oauth2SuccessHandler)
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .failureHandler(loginFailureHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/users/logout")
                        .logoutSuccessUrl("/next-page")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );
        return http.build();
    }
}