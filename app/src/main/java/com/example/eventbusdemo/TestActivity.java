package com.example.eventbusdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.simpleeventbus.EventBus;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        findViewById(R.id.bt_main).setOnClickListener(this);
        findViewById(R.id.bt_thread).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_main:
                Log.e("Event", "post in " + Thread.currentThread().getName());
                EventBus.get().post(new TestEvent());
                break;
            case R.id.bt_thread:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("Event", "post in " + Thread.currentThread().getName());
                        EventBus.get().post(new TestEvent());
                    }
                }).start();
                break;
        }

        Toast.makeText(this, "事件发送成功,结果请看Log(Event)打印", Toast.LENGTH_LONG).show();
    }
}