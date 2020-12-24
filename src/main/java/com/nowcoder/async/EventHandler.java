package com.nowcoder.async;

import java.util.List;


public interface EventHandler {
    void doHandle(EventModel model);   //对事件模型进行处理

    List<EventType> getSupportEventTypes();    //该处理器支持的事件类型
}
