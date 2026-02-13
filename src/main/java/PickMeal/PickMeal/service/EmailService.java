package PickMeal.PickMeal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // application.properties에 설정한 이메일 계정을 불러옵니다.
    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 인증번호 생성 (6자리 랜덤 숫자)
     */
    public String createCode() {
        // (int)(Math.random() * 899999) + 100000; 로직은 100000 ~ 999999 사이를 생성합니다.
        return String.valueOf((int)(Math.random() * 899999) + 100000);
    }

    /**
     * 메일 발송 로직
     * @param toEmail 수신자 이메일
     * @param code 인증번호
     */
    public void sendMail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("[Pick Meal] 회원가입 인증번호입니다. ✉️");

        // 메일 본문 구성
        StringBuilder sb = new StringBuilder();
        sb.append("안녕하세요! Pick Meal입니다.\n\n");
        sb.append("요청하신 회원가입 인증번호는 다음과 같습니다.\n");
        sb.append("인증번호: [ ").append(code).append(" ]\n\n");
        sb.append("인증 유효 시간은 5분입니다. 시간 내에 입력해 주세요.");

        message.setText(sb.toString());

        // 보안을 위해 설정 파일에서 가져온 이메일을 사용합니다.
        // "이름 <이메일>" 형식을 쓰면 받는 사람 메일함에 이름이 예쁘게 표시됩니다.
        message.setFrom("PickMeal <" + fromEmail + ">");

        mailSender.send(message);
    }
}