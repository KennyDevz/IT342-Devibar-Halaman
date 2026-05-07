package edu.cit.devibar.halaman.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendVerificationEmail(String toEmail, String firstName, String otpCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("🌿 Your Halaman Verification Code");

            String htmlContent = String.format("""
                <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; padding: 30px; border: 1px solid #e2e8f0; border-radius: 12px; background-color: #ffffff;">
                    
                    <div style="text-align: center; margin-bottom: 30px;">
                        <h1 style="color: #16a34a; margin: 0; font-size: 28px; letter-spacing: -0.5px;">🌿 Halaman</h1>
                    </div>
                    
                    <h2 style="color: #1e293b; font-size: 20px; font-weight: 600; margin-bottom: 15px;">Hello, %s!</h2>
                    
                    <p style="color: #475569; font-size: 16px; line-height: 1.6; margin-bottom: 25px;">
                        We're thrilled to have you in our community. To complete your registration and secure your digital garden, please enter the verification code below:
                    </p>
                    
                    <div style="text-align: center; margin: 35px 0;">
                        <span style="display: inline-block; font-size: 36px; font-weight: 700; letter-spacing: 8px; color: #15803d; background-color: #f0fdf4; padding: 20px 40px; border-radius: 10px; border: 2px dashed #86efac;">
                            %s
                        </span>
                    </div>

                    <div style="text-align: center; margin-bottom: 30px;">
                        <a href="http://localhost:5173/verify?email=%s" style="background-color: #16a34a; color: #ffffff; padding: 12px 24px; text-decoration: none; border-radius: 8px; font-weight: 600; display: inline-block; font-size: 16px;">
                            Go to Verification Page
                        </a>
                    </div>
                    
                    <p style="color: #64748b; font-size: 14px; line-height: 1.5; text-align: center; background-color: #f8fafc; padding: 15px; border-radius: 8px;">
                        <strong>Security Note:</strong> This code will expire in 10 minutes. Please do not share this code with anyone.
                    </p>
                    
                    <hr style="border: none; border-top: 1px solid #e2e8f0; margin: 30px 0;" />
                    
                    <p style="text-align: center; color: #94a3b8; font-size: 13px; margin: 0;">
                        Happy Planting! 🌱<br/>
                        <strong>The Halaman Team</strong>
                    </p>
                </div>
                """, firstName, otpCode, toEmail);

            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Failed to send HTML email: " + e.getMessage());
            throw new RuntimeException("Failed to send email");
        }
    }

    @Async
    public void sendDeletionWarningEmail(String toEmail, String firstName, String plantNickname, int daysRemaining) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("⚠️ Notice: '" + plantNickname + "' is scheduled for permanent deletion");

            String htmlContent = String.format("""
                <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; max-width: 600px; margin: 0 auto; padding: 30px; border: 1px solid #e2e8f0; border-radius: 12px; background-color: #fffaf5;">
                    
                    <div style="text-align: center; margin-bottom: 30px;">
                        <h1 style="color: #ea580c; margin: 0; font-size: 24px;">Halaman System Notice</h1>
                    </div>
                    
                    <div style="background-color: #ffffff; padding: 25px; border-radius: 8px; border-left: 4px solid #f97316; box-shadow: 0 1px 3px rgba(0,0,0,0.1);">
                        <h2 style="color: #1e293b; font-size: 18px; margin-top: 0;">Hello, %s!</h2>
                        
                        <p style="color: #475569; font-size: 16px; line-height: 1.6;">
                            This is an automated system notification. We noticed that your plant, <strong>%s</strong>, has been in your Recycle Bin for a while.
                        </p>
                        
                        <div style="text-align: center; margin: 25px 0; padding: 15px; background-color: #ffedd5; border-radius: 8px;">
                            <span style="color: #c2410c; font-size: 18px; font-weight: bold;">
                                Scheduled for permanent deletion in %d days.
                            </span>
                        </div>
                        
                        <p style="color: #475569; font-size: 15px;">
                            If you meant to delete this plant, no further action is required. It will be securely wiped from our database. If this was a mistake, please log in and restore the plant from your Recycle Bin before the deadline.
                        </p>
                    </div>
                    
                    <p style="text-align: center; color: #94a3b8; font-size: 12px; margin-top: 30px;">
                        This is an automated system notification. Please do not reply to this email.<br/>
                        <strong>Halaman Data Lifecycle Management</strong>
                    </p>
                </div>
                """, firstName, plantNickname, daysRemaining);

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Failed to send deletion warning email: " + e.getMessage());
        }
    }
}