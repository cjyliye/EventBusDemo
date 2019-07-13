package com.example.simpleeventbus;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

class ExecuteHandler extends Handler {
    private EventBus eventBus;

    public ExecuteHandler(Looper looper, EventBus eventBus) {
        super(looper);
        this.eventBus = eventBus;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        //获取要执行的信息
        ExecuteInfo executeInfo = (ExecuteInfo) msg.obj;
        eventBus.invoke(executeInfo);
    }


    public void post(ExecuteInfo executeInfo){
        Message message = Message.obtain();
        message.obj = executeInfo;
        sendMessage(message);
    }
}