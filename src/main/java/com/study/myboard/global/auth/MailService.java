package com.study.myboard.global.auth;

import com.study.myboard.global.exception.CustomErrorCode;
import com.study.myboard.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.study.myboard.global.exception.CustomErrorCode.EMAIL_SEND_ERROR;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender emailSender;

    // 이메일 발송
    public void sendEmail(String email, String title, String authCode) {
        String content = "요청하신 인증번호는 [ "+authCode+" ] 입니다.\n감사합니다.";
        SimpleMailMessage emailForm = createEmailForm(email, title, content);
        try {
            emailSender.send(emailForm);
        } catch (RuntimeException e) {
            throw new CustomException(EMAIL_SEND_ERROR);
        }
    }

    // 발송할 이메일 데이터 설정
    private SimpleMailMessage createEmailForm(String email, String title, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(title);
        message.setText(text);

        return message;
    }
}
