package com.example.caps;

import android.os.Handler;

public class ServiceThread extends Thread {

    Handler handler;

    public ServiceThread(Handler handler){
        this.handler = handler;
    }

    public void run(){

        try{

            Thread.sleep(10000);
            handler.sendEmptyMessage(0);//쓰레드에 있는 핸들러에게 메세지를 보냄
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
