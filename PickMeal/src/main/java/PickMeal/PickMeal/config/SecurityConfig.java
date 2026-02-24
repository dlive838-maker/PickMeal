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
                .csrf(csrf -> csrf.ignoringRequestMatchers("/mail/**", "/worldcup/win/**"))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/next-page", "/users/signup", "/users/signup/social", "/users/login",
                                "/users/check-id", "/users/check-nickname", "/users/mypage", "/mail/**",
                                "/oauth2/**", "/css/**", "/js/**", "/images/**", "/worldcup/win/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/users/login")
                        .usernameParameter("id")
                        .defaultSuccessUrl("/next-page", true)
                        .failureHandler(loginFailureHandler) // 일반 로그인 실패 핸들러
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/users/login")
                        .successHandler(oauth2SuccessHandler)
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        // [핵심 추가] 소셜 로그인 실패 시에도 동일한 핸들러를 사용하도록 설정합니다.
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