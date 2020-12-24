package com.nowcoder.async;

import java.util.HashMap;
import java.util.Map;


public class EventModel {   //定义事件模型
    private EventType type;   //事件类型：点赞、评论、登录、邮件、关注、取消关注、提问
    private int actorId;      //事件触发者的id
    private int entityType;   //实体类型
    private int entityId;     //实体id
    private int entityOwnerId;  //实体所有者的id

    private Map<String, String> exts = new HashMap<>();   //拓展字段

    public EventModel() {

    }

    public EventModel setExt(String key, String value) {  //各种get和set方法
        exts.put(key, value);
        return this;
    }

    public EventModel(EventType type) {
        this.type = type;
    }

    public String getExt(String key) {
        return exts.get(key);
    }


    public EventType getType() {
        return type;
    }

    public EventModel setType(EventType type) {
        this.type = type;
        return this;
    }

    public int getActorId() {
        return actorId;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExts() {
        return exts;
    }

    public EventModel setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }
}
