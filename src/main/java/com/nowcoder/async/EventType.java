package com.nowcoder.async;


public enum EventType {      //枚举了7种事件类型，包括：点赞、评论、登录、邮件、关注、取消关注、提问    邮件功能还有问题
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3),
    FOLLOW(4),
    UNFOLLOW(5),
    ADD_QUESTION(6);

    private int value;
    EventType(int value) { this.value = value; }
    public int getValue() { return value; }
}
