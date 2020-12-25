package com.nowcoder.async;

import com.alibaba.fastjson.JSON;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    //EventConsumer 为了让Bean获取它所在的Spring容器，可以让该Bean实现ApplicationContextAware接口。
    //在spring初始化bean的时候，如果bean实现了InitializingBean接口，会自动调用afterPropertiesSet方法。
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType, List<EventHandler>> config = new HashMap<>();   //使用map集合来存放初始配置，存放该事件类型对应的事件处理器
    private ApplicationContext applicationContext;  //上下文

    @Autowired
    JedisAdapter jedisAdapter;   //将jedis操作封装起来，也是一个bean

    @Override
    public void afterPropertiesSet() throws Exception {   //初始化bean时的操作
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);    //从上下文中获取所有的bean和对应的handler
        if (beans != null) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {   //遍历map,得到每一个eventType都由哪些处理器来处理，存入另一个哈希表config中
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();  //得到当前eventHandler能处理哪些eventType

                for (EventType type : eventTypes) {   //遍历eventType
                    if (!config.containsKey(type)) {
                        config.put(type, new ArrayList<>());
                    }
                    config.get(type).add(entry.getValue());
                }
            }
        }

        Thread thread = new Thread(() -> {
            while(true) {
                String key = RedisKeyUtil.getEventQueueKey();   //生成redis所要存储的key：eventQueue
                List<String> events = jedisAdapter.brpop(0, key);  //列表阻塞式弹出

                for (String message : events) {
                    if (message.equals(key)) {  //先弹出key，再弹出value，这里将key过滤
                        continue;
                    }

                    EventModel eventModel = JSON.parseObject(message, EventModel.class);
                    if (!config.containsKey(eventModel.getType())) {
                        logger.error("不能识别的事件");
                        continue;
                    }

                    for (EventHandler handler : config.get(eventModel.getType())) {   //遍历该eventType对应的handler，依次进行处理
                        handler.doHandle(eventModel);
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
