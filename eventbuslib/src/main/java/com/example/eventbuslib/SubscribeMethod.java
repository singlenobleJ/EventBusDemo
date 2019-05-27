package com.example.eventbuslib;

import java.lang.reflect.Method;

/**
 * @author: lilinjie
 * @date: 2019-05-25 09:25
 * @description: 将订阅方法进行对象的封装
 */
public class SubscribeMethod {
    private Method method;
    private Class<?> type;
    private ThreadMode threadMode;

    public SubscribeMethod(Method method, Class<?> type, ThreadMode threadMode) {
        this.method = method;
        this.type = type;
        this.threadMode = threadMode;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(ThreadMode threadMode) {
        this.threadMode = threadMode;
    }
}
