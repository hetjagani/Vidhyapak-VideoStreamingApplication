package cc.app.microservice.VideoUploadService.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailUtil {

    private static final String USERNAME = "tempanonymous1999@gmail.com";
    private static final String PASSWORD = "temp@123456789";
    private static final String EMAIL_FROM = "tempanonymous1999@gmail.com";
    private static final String EMAIL_TO_CC = "";

    public static int sendMail(String text, String subject, String targetMail) {

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USERNAME, PASSWORD);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(targetMail)
            );
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);

            System.out.println("Done");
            return 0;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return -1;
    }
}