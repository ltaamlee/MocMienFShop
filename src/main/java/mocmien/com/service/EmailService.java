package mocmien.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("letam41225@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Mã OTP xác thực - Mộc Miên Flower Shop");
            message.setText("Mã OTP của bạn là: " + otp + "\nThời hạn: 5 phút.");

            mailSender.send(message);
            System.out.println("✅ Email OTP đã được gửi đến: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ Gửi mail thất bại: " + e.getMessage());
        }
    }
}