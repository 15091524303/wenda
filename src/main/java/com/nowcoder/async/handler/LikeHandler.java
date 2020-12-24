package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHandler {   //对点赞事件的处理
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        message.setFromId(WendaUtil.SYSTEM_USERID);  //信息由系统发出
        message.setToId(model.getEntityOwnerId());   //接收者的id
        message.setCreatedDate(new Date());   //发起时间
        User user = userService.getUser(model.getActorId());   //是谁点赞的
        message.setContent("用户" + user.getName()
                + "赞了你的评论,http://127.0.0.1:8080/question/" + model.getExt("questionId"));

        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Collections.singletonList(EventType.LIKE);  //处理点赞事件
    }  //能处理的事件类型：点赞
}
