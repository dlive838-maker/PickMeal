package PickMeal.PickMeal.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException; // 반드시 이 경로여야 합니다!
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        // 보안을 위해 상세 원인을 숨기고 통합 메시지 설정
        String errorMessage = "아이디 또는 비밀번호가 일치하지 않습니다.";

        // 기타 다른 예외 상황(계정 잠금 등)에 대해서도 동일하게 처리하는 것이 안전합니다.

        // 세션에 통합 메시지 저장
        request.getSession().setAttribute("errorMessage", errorMessage);

        // 로그인 페이지로 리다이렉트
        response.sendRedirect(request.getContextPath() + "/users/login");
    }
}