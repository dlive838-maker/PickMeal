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
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/mail/**", "/users/**", "/worldcup/win/**", "/hotplace/**",
                        "/board/write", "/file/upload", "/api/wishlist/**", "/api/restaurant/**", "/api/review/**"
                ))
                .authorizeHttpRequests(authorize -> authorize
                        // 1. [구체적인 예외] 게시판 중 'meal-spotter'는 로그인 없이도 볼 수 있게 최상단에 둡니다.
                        .requestMatchers("/board/meal-spotter").permitAll()

                        // 2. [로그인 필수] 글쓰기, 수정, 삭제 등은 로그인이 필요합니다.
                        .requestMatchers("/users/mypage", "/users/edit", "/board/write", "/board/edit/**", "/board/remove/**").authenticated()

                        // 3. [공통 허용] 나머지 모든 정적 리소스와 게임, API 경로들을 허용합니다.
                        .requestMatchers("/", "/next-page", "/hotplace", "/board/**", // 상세보기도 로그인 없이 가능하게 하려면 여기에 포함
                                "/users/signup", "/users/signup/social", "/users/login",
                                "/users/check-id", "/users/check-nickname", "/users/find-id",
                                "/users/forgot-pw", "/users/find-password/**", "/users/reset-password/**",
                                "/mail/**", "/oauth2/**", "/css/**", "/js/**", "/images/**", "/worldcup/win/**",
                                "/roulette", "/twentyQuestions/**", "/twenty-questions/**", "/capsule", "/game/**", "/worldcup/**",
                                "/api/**", "/draw", "/meal-spotter", "/hotplace/**", "/*.json").permitAll()

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