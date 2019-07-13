package com.example.eventbusdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.simpleeventbus.EventBus;
import com.example.simpleeventbus.Subscribe;
import com.example.simpleeventbus.ThreadMode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bt_next).setOnClickListener(this);
        findViewById(R.id.bt_register).setOnClickListener(this);
        findViewById(R.id.bt_unregister).setOnClickListener(this);
    }

    @Subscribe
    public void posting(TestEvent event) {
        Log.e("Event", "posting threadMode receive successful in " + Thread.currentThread().getName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void main(TestEvent event) {
        Log.e("Event", "main threadMode receive successful in " + Thread.currentThread().getName());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void background(TestEvent event) {
        Log.e("Event", "background threadMode receive successful in " + Thread.currentThread().getName());

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.get().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_next:
                startActivity(new Intent(MainActivity.this, TestActivity.class));
                break;
            case R.id.bt_register:
                try {
                    //我们里面逻辑判断过如果订阅过再发起订阅的话会抛出异常，但其实不止重复订阅会抛出异常
                    //还有订阅方法参数不符合等等会抛出异常，这里只是为了简单的测试
                    EventBus.get().register(MainActivity.this);
                    Toast.makeText(MainActivity.this,"订阅成功",Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(MainActivity.this,"此类已经订阅过",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.bt_unregister:
                EventBus.get().unregister(MainActivity.this);
                Toast.makeText(MainActivity.this,"取消订阅成功",Toast.LENGTH_LONG).show();
                break;
        }
    }
}
