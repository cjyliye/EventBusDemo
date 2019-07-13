package com.example.simpleeventbus;

/**
 * 执行体
 */
public class ExecuteInfo {
    private SubscribeInfo subscribeInfo;
    private Object event;

    public ExecuteInfo(SubscribeInfo subscribeInfo, Object event) {
        this.subscribeInfo = subscribeInfo;
        this.event = event;
    }

    public SubscribeInfo getSubscribeInfo() {
        return subscribeInfo;
    }

    public void setSubscribeInfo(SubscribeInfo subscribeInfo) {
        this.subscribeInfo = subscribeInfo;
    }

    public Object getEvent() {
        return event;
    }

    public void setEvent(Object event) {
        this.event = event;
    }
}
