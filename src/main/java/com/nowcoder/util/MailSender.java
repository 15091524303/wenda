package com.nowcoder.util;

import com.nowcoder.model.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.util.Properties;

//@Service
public class MailSender implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
    @Autowired
    private JavaMailSenderImpl mailSender;

//    @Autowired
//    private VelocityEngine velocityEngine;


    @Autowired
    private TemplateEngine templateEngine;

    public boolean sendWithHTMLTemplate(Mail mail) {
        try {
            String nick = MimeUtility.encodeText("20461855302");  //设置昵称
            InternetAddress from = new InternetAddress(nick + "<"+nick+"@nowcoder.com>");  //发件人
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            //是否发送的邮件是富文本（附件，图片，html等）
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
//            String result = VelocityEngineUtils
//                    .mergeTemplateIntoString(velocityEngine, template, "UTF-8", model);
            mimeMessageHelper.setTo(mail.getEmail());
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(mail.getTitle());

            //使用模板thymeleaf
            //Context是导这个包import org.thymeleaf.context.Context;
            Context context = new Context();
            //定义模板数据
            context.setVariables(mail.getAttachment());
            //获取thymeleaf的html模板
            String emailContent = templateEngine.process("mails/login_exception.html",context); //指定模板路径

            mimeMessageHelper.setText(emailContent, true);
            mailSender.send(mimeMessage);
            return true;
        } catch (Exception e) {
            logger.error("发送邮件失败" + e.getMessage());
            return false;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        mailSender = new JavaMailSenderImpl();
        mailSender.setUsername("course@nowcoder.com");
        mailSender.setPassword("NKnk123");
        mailSender.setHost("smtp.exmail.qq.com");
        //mailSender.setHost("smtp.qq.com");
        mailSender.setPort(465);
        mailSender.setProtocol("smtps");
        mailSender.setDefaultEncoding("utf8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable", true);
        //javaMailProperties.put("mail.smtp.auth", true);
        //javaMailProperties.put("mail.smtp.starttls.enable", true);
        mailSender.setJavaMailProperties(javaMailProperties);
    }
}
