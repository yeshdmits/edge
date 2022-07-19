package yeshenko.edge.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static yeshenko.edge.constant.Constants.EMAIL_FROM;
import static yeshenko.edge.constant.Constants.EMAIL_SUBJECT;
import static yeshenko.edge.constant.Constants.EMAIL_TO;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void send(String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom(EMAIL_FROM);
        simpleMailMessage.setTo(EMAIL_TO);
        simpleMailMessage.setSubject(EMAIL_SUBJECT);
        simpleMailMessage.setText(message);

        mailSender.send(simpleMailMessage);
        log.info("Email has been send: {}", message);
    }
}
