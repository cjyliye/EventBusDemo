package com.example.simpleeventbus;

import java.lang.reflect.Method;

/**
 * 订阅信息体
 */
public class SubscribeInfo {
    private Object subscriber;
    private Method method;
    private Class<?> eventType;
    private ThreadMode threadMode;

    public SubscribeInfo(Object subscriber, Method method, Class<?> eventType, ThreadMode threadMode) {
        this.subscriber = subscriber;
        this.method = method;
        this.eventType = eventType;
        this.threadMode = threadMode;
    }

    public Object getSubscriber() {
        return subscriber;
    }

    public void setSubsciber(Object subsciber) {
        this.subscriber = subsciber;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public void setEventType(Class<?> eventType) {
        this.eventType = eventType;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(ThreadMode threadMode) {
        this.threadMode = threadMode;
    }
}
