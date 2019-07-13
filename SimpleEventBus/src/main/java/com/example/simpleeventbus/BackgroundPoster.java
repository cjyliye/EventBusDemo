package com.example.simpleeventbus;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 子线程发送器，通过线程池发送事件
 */
public class BackgroundPoster {
    private EventBus eventBus;
    private ExecutorService executor =  Executors.newCachedThreadPool();

    public BackgroundPoster(EventBus eventBus){
        this.eventBus = eventBus;
    }

    public void post(ExecuteInfo executeInfo) {
        executor.execute(new Poster(executeInfo, eventBus));
    }

    private class Poster implements Runnable {
        private ExecuteInfo executeInfo;
        private EventBus eventBus;

        public Poster(ExecuteInfo executeInfo, EventBus eventBus) {
            this.executeInfo = executeInfo;
            this.eventBus = eventBus;
        }

        @Override
        public void run() {
            eventBus.invoke(executeInfo);
        }
    }
}
