package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Mail;
import com.nowcoder.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nowcoder on 2016/7/30.
 */
@Component
public class LoginExceptionHandler implements EventHandler {

//    @Autowired
    MailSender mailSender;


    @Override
    public void doHandle(EventModel model) {
        // xxxx判断发现这个用户登陆异常
        Map<String, Object> map = new HashMap<>();
        map.put("username", model.getExt("username"));
        Mail mail = new Mail();
        mail.setTitle("登陆IP异常");
        mail.setEmail(model.getExt("email"));
        mail.setContent("mails/login_exception.html");
        mail.setAttachment(map);
        mailSender.sendWithHTMLTemplate(mail);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
