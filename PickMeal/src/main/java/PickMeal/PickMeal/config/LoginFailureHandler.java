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

        // 1. 기본 메시지 설정
        String errorMessage = "아이디 또는 비밀번호가 일치하지 않습니다.";

        // 2. 디버깅을 위해 콘솔에 에러 정보를 상세히 출력해보세요 (개발 단계에서 필수!)
        System.out.println("### Login Failure Exception: " + exception.getClass().getName());
        System.out.println("### Exception Message: " + exception.getMessage());

        // 3. 소셜 로그인 예외(OAuth2AuthenticationException)인지 확인
        if (exception instanceof org.springframework.security.oauth2.core.OAuth2AuthenticationException) {
            org.springframework.security.oauth2.core.OAuth2AuthenticationException oauthException =
                    (org.springframework.security.oauth2.core.OAuth2AuthenticationException) exception;

            // 에러 코드나 메시지에 "withdrawn_user"가 포함되어 있는지 확인
            String errorCode = oauthException.getError().getErrorCode();
            if ("withdrawn_user".equals(errorCode) || (exception.getMessage() != null && exception.getMessage().contains("withdrawn_user"))) {
                errorMessage = "탈퇴 처리된 계정입니다. 고객센터에 문의해주세요.";
            }
        }
        // 4. 일반 로그인 혹은 기타 예외에서 메시지 포함 여부 확인
        else if (exception.getMessage() != null && exception.getMessage().contains("withdrawn_user")) {
            errorMessage = "탈퇴 처리된 계정입니다. 고객센터에 문의해주세요.";
        }

        // 5. 세션에 메시지 저장
        request.getSession().setAttribute("errorMessage", errorMessage);

        // 6. 로그인 페이지로 리다이렉트
        response.sendRedirect(request.getContextPath() + "/users/login");
    }
}