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
                .csrf(csrf -> csrf.ignoringRequestMatchers("/mail/**", "/worldcup/win/**", "/users/login"))
                .authorizeHttpRequests(authorize -> authorize
                        // [수정] /next 경로를 추가하여 인트로에서 넘어갈 수 있게 허용합니다.
                        .requestMatchers("/", "/next-page", "/users/signup", "/users/signup/social", "/users/login",
                                "/users/check-id", "/users/check-nickname", "/users/mypage", "/mail/**",
                                "/oauth2/**", "/css/**", "/js/**", "/images/**", "/worldcup/win/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/users/login")
                        // ★ 추가: 문지기에게 "여기(/users/login)로 들어오는 POST 요청이 진짜 로그인 서류야!"라고 확실히 알려줍니다.
                        .loginProcessingUrl("/users/login")
                        .usernameParameter("id")
                        .defaultSuccessUrl("/next-page", true)
                        .failureHandler(loginFailureHandler)
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/users/login")
                        .successHandler(oauth2SuccessHandler)
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                )
                .logout(logout -> logout
                        // [수정] 로그아웃 주소 통일
                        .logoutUrl("/users/logout")
                        .logoutSuccessUrl("/next-page") // [수정 완료] 로그아웃 후 /next 페이지로 이동합니다
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );
        return http.build();
    }
}