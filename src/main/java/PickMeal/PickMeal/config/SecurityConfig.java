package PickMeal.PickMeal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 이 클래스는 '설정용 부품'이라고 스프링에게 알려줌.
@EnableWebSecurity // 우리 웹사이트에 보안 기능을 가동시킴.
public class SecurityConfig {

    @Bean // 이 메소드가 만드는 '보안 규칙'을 스프링이 관리하도록 등록.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 보안 검사(CSRF)를 잠시 끔. (개발 단계에서는 켜져 있으면 테스트가 어렵기 때문.)
                .csrf(csrf -> csrf.disable())

                // 2. 페이지별 접근 권한 설정 (입구 컷 규칙)
                .authorizeHttpRequests(auth -> auth
                        // 메인화면, 월드컵 게임, CSS/JS 파일은 로그인 안 해도 누구나 볼 수 있게 허용.
                        .requestMatchers("/", "/worldcup.html", "/css/**", "/js/**", "/images/**", "/static/**").permitAll()
                        // 그 외의 모든 페이지(예: 마이페이지)는 반드시 로그인을 해야만 들어올 수 있음.
                        .anyRequest().authenticated()
                )

                // 3. 소셜 로그인 설정 연결
                .oauth2Login(oauth2 -> oauth2
                        // 네이버 로그인이 성공하면 자동으로 '/worldcup.html' 페이지로 보냄.
                        //.defaultSuccessUrl("/worldcup", true)
                        .defaultSuccessUrl("/", true)
                )

                 .logout(logout -> logout
                .logoutUrl("/logout") // 로그아웃을 수행할 주소 (주문서 이름)
                .logoutSuccessUrl("/") // 로그아웃 성공 시 다시 원래 홈페이지로 이동
                .invalidateHttpSession(true) // 서버에 저장된 손님 정보를 완전히 지움 (세션 무효화)
                .deleteCookies("JSESSIONID") // 브라우저에 남은 흔적(쿠키)까지 삭제.
                         .permitAll()
        );



        return http.build(); // 완성된 보안 규칙을 반환.
    }
}