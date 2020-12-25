package com.nowcoder.util;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
public class SendMail{
    public static SendMail sMessage;
    private MimeMessage message;
    private Properties props = new Properties();
    public static String userName;
    public static String password;
    private static Session mailSession;
    private static InternetAddress to;

    SendMail() {
        try {
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", "smtp.qq.com");
            props.put("mail.smtp.port", "587");//接收邮件服务器：pop.qq.com，使用SSL，端口号995  //发送邮件服务器：smtp.qq.com，使用SSL，端口号465或587

            props.put("mail.user", "2046185302@qq.com");//发送出去邮箱，
            props.put("mail.password", "36X2718YZ9");

            // 用户名、密码
            userName = props.getProperty("mail.user");
            password = props.getProperty("mail.password");

            // 构建授权信息，用于进行SMTP进行身份验证
            Authenticator authenticator = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(userName, password);
                }
            };
            mailSession = Session.getInstance(props, authenticator);
            message = new MimeMessage(mailSession);
            InternetAddress form = new InternetAddress(props.getProperty("mail.user"));
            message.setFrom(form);
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /** 参数， 发送的邮件的标题，邮件的内容，目标邮箱地址 **/
    public void sendEm(String str, String cont, String mailbox) {
        try {
            // 添加目标邮箱地址
            to = new InternetAddress(mailbox);
            message.setRecipient(RecipientType.TO, to);
            // 设置邮件标题
            message.setSubject(str);
            // 设置邮件的内容体
            message.setContent(cont, "text/html;charset=UTF-8");
            Transport.send(message);
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new SendMail().sendEm("hahhahahhaha", "哈哈哈哈哈哈", "2046185302@qq.com");
    }
}
