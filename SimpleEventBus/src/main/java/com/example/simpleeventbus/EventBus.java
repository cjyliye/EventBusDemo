package com.example.simpleeventbus;


import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus {
    private static volatile EventBus instance;
    //存放事件类型对应的订阅者信息
    private Map<Class<?>, List<SubscribeInfo>> eventForSubscriber;
    //存放订阅者对应的订阅事件类型
    private Map<Object, List<Class<?>>> subscribeType;
    private ExecuteHandler handler;
    private BackgroundPoster backgroundPoster;

    public static EventBus get() {
        if (instance == null) {
            synchronized (EventBus.class) {
                if (instance == null) {
                    instance = new EventBus();
                }
            }
        }
        return instance;
    }

    private EventBus() {
        eventForSubscriber = new ConcurrentHashMap<>();
        subscribeType = new ConcurrentHashMap<>();
        handler = new ExecuteHandler(Looper.getMainLooper(), this);
        backgroundPoster = new BackgroundPoster(this);
    }

    /**
     * 注册订阅
     *
     * @param subscriber
     */
    public void register(Object subscriber) {
        synchronized (this) {
            if (subscribeType.get(subscriber) != null) {
                throw new RuntimeException(subscriber.getClass().getName() + " have been registered");
            }
            List<SubscribeInfo> subscribeInfoList = getSubscribeInfo(subscriber);
            for (SubscribeInfo subscribeInfo : subscribeInfoList) {
                saveInfo(subscribeInfo);
            }
        }
    }

    /**
     * 将订阅信息保存起来
     *
     * @param subscribeInfo
     */
    private void saveInfo(SubscribeInfo subscribeInfo) {
        Class<?> eventType = subscribeInfo.getEventType();
        //获取此事件类型对应的订阅者列表
        List<SubscribeInfo> subscribeInfoList = eventForSubscriber.get(eventType);
        if (subscribeInfoList == null) {
            subscribeInfoList = new ArrayList<>();
            eventForSubscriber.put(eventType, subscribeInfoList);
        }
        //将新的订阅者添加进列表
        subscribeInfoList.add(subscribeInfo);
        //获取该订阅者类订阅的事件类型
        List<Class<?>> eventTypes = subscribeType.get(subscribeInfo.getSubscriber());
        if (eventTypes == null) {
            eventTypes = new ArrayList<>();
            subscribeType.put(subscribeInfo.getSubscriber(), eventTypes);
        }
        if (!eventTypes.contains(eventType)) {
            //添加新类型
            eventTypes.add(eventType);
        }
    }

    /**
     * 获取注册类中的订阅方法
     *
     * @param subscriber
     * @return
     */
    private List<SubscribeInfo> getSubscribeInfo(Object subscriber) {
        List<SubscribeInfo> subscribeInfoList = new ArrayList<>();
        Class<?> subscriberClass = subscriber.getClass();
        //反射得到类中的方法
        Method[] methods = subscriberClass.getDeclaredMethods();
        for (Method method : methods) {
            Subscribe annotation = method.getAnnotation(Subscribe.class);
            //如果是订阅方法
            if (annotation != null) {
                //如果方法不是publish的抛出异常
                if (method.getModifiers() != Modifier.PUBLIC)
                    throw new RuntimeException("subscribe method must be public");
                //获取参数列表
                Class<?>[] parameterTypes = method.getParameterTypes();
                //获取线程模型
                ThreadMode threadMode = annotation.threadMode();
                //我们的event只支持一个参数
                if (parameterTypes.length == 1) {
                    SubscribeInfo subscribeInfo = new SubscribeInfo(subscriber, method, parameterTypes[0], threadMode);
                    subscribeInfoList.add(subscribeInfo);
                } else {
                    throw new RuntimeException(subscriberClass.getName() + " subscribe method supports only one parameter");
                }
            }
        }
        return subscribeInfoList;
    }

    /**
     * 取消订阅
     *
     * @param subscriber
     */
    public void unregister(Object subscriber) {
        synchronized (this) {
            List<Class<?>> eventTypes = subscribeType.get(subscriber);
            if (eventTypes != null) {
                for (Class<?> eventType : eventTypes) {
                    unsubscribe(subscriber, eventType);
                }
            }
            subscribeType.remove(subscriber);
        }
    }

    /**
     * 取消该类中对该事件的订阅
     */
    private void unsubscribe(Object subscriber, Class eventType) {
        List<SubscribeInfo> subscribeInfoList = eventForSubscriber.get(eventType);
        Iterator iterator = subscribeInfoList.iterator();
        while (iterator.hasNext()) {
            SubscribeInfo subscribeInfo = (SubscribeInfo) iterator.next();
            if (subscribeInfo.getSubscriber() == subscriber) {
                iterator.remove();
            }
        }
    }

    /**
     * 发送事件
     *
     * @param event
     */
    public void post(Object event) {
        Class eventType = event.getClass();
        List<SubscribeInfo> subscribeInfoList = eventForSubscriber.get(eventType);
        if (subscribeInfoList == null) return;
        for (SubscribeInfo subscribeInfo : subscribeInfoList) {
            ExecuteInfo executeInfo = new ExecuteInfo(subscribeInfo, event);
            //根据线程模型来进行相应操作
            switch (subscribeInfo.getThreadMode()) {
                case MAIN:
                    if (isMain()) {
                        invoke(executeInfo);
                    } else {
                        handler.post(executeInfo);
                    }
                    break;
                case POSTING:
                    invoke(executeInfo);
                    break;
                case BACKGROUND:
                    if (isMain()) {
                        backgroundPoster.post(executeInfo);
                    } else {
                        invoke(executeInfo);
                    }
                    break;
            }
        }
    }

    private boolean isMain(){
        return Looper.getMainLooper() == Looper.myLooper();
    }

    void invoke(ExecuteInfo executeInfo) {
        SubscribeInfo subscribeInfo = executeInfo.getSubscribeInfo();
        Object event = executeInfo.getEvent();
        try {
            //执行method
            subscribeInfo.getMethod().invoke(subscribeInfo.getSubscriber(), event);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
