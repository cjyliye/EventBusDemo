package com.example.simpleeventbus;

public enum ThreadMode {
    /**
     * 回调在发布线程中
     */
    POSTING,

    /**
     * 回调在主线程
     */
    MAIN,

    /**
     * 回调在异步线程
     */
    BACKGROUND

}
