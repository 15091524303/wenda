package com.nowcoder.model;

import org.springframework.stereotype.Component;

@Component
public class HostHolder {   //将用户信息存在线程池中
    //ThreadLocal提供了线程内存储变量的能力，这些变量不同之处在于每一个线程读取的变量是对应的互相独立的。通过get和set方法就可以得到当前线程对应的值。
    private static ThreadLocal<User> users = new ThreadLocal<>();

    public User getUser() {
        return users.get();
    }    //得到当前线程

    public void setUser(User user) {
        users.set(user);
    }   //设置当前线程

    public void clear() {
        users.remove();
    }    //清空当前线程
}
