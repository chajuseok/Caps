package com.example.caps;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyService extends Service {

    NotificationManager Notifi_M;
    ServiceThread thread;
    Notification Notifi ;

    String t; // 시간
    String access_token;
    String fintech_use_num;
    String bank_number;
    String recv_user;
    String money;


    public MyService() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread = null;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        access_token =  intent.getStringExtra("access_token");
        fintech_use_num = intent.getStringExtra("fintech_use_num");
        bank_number = intent.getStringExtra("num");
        money = intent.getStringExtra("mon");
        t = intent.getStringExtra("t");

        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread(handler);
        thread.start();
        return START_STICKY;
    }


    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            Intent intent = new Intent(MyService.this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            Notifi = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Content Title")
                    .setContentText("Content Text")
                    .setSmallIcon(R.drawable.bot)
                    .setTicker("알림!!!")
                    .setContentIntent(pendingIntent)
                    .build();

            //소리추가
            Notifi.defaults = Notification.DEFAULT_SOUND;

            //알림 소리를 한번만 내도록
            Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;

            //확인하면 자동으로 알림이 제거 되도록
            Notifi.flags = Notification.FLAG_AUTO_CANCEL;


            Notifi_M.notify( 777 , Notifi);

            //토스트 띄우기
            Toast.makeText(MyService.this, "송금 완료", Toast.LENGTH_LONG).show();

            Withdraw th = new Withdraw();
            th.start();
            try {
                th.join();
            }catch(InterruptedException e)
            {
                e.printStackTrace();
            }

        }
    };


    public void Withdraw()
    {
        String address = "https://testapi.openbanking.or.kr/v2.0/transfer/withdraw/fin_num";
        String PostBody = "{\r\n"
                + "    \"bank_tran_id\": \""+GenTranId()+"\",\r\n"
                + "    \"cntr_account_type\": \"N\",\r\n"
                + "    \"cntr_account_num\": \""+bank_number+"\",\r\n"
                + "    \"dps_print_content\": \"테스트\",\r\n"
                + "    \"fintech_use_num\": \""+fintech_use_num+"\",\r\n"
                + "    \"tran_amt\": \""+money+"\",\r\n"
                + "    \"tran_dtime\": \"20220401113210\",\r\n"
                + "    \"req_client_name\": \"김주열\",\r\n"
                + "    \"req_client_num\": \"1101005239\",\r\n"
                + "    \"transfer_purpose\": \"TR\",\r\n"
                + "    \"req_client_fintech_use_num\": \"120220055688941039534617\",\r\n"
                + "    \"recv_client_name\": \"차주석\",\r\n"
                + "    \"recv_client_bank_code\": \"007\",\r\n"
                + "    \"recv_client_account_num\": \"4564564564\"\r\n"
                + "}";
        try
        {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization",access_token);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            try(OutputStream os = connection.getOutputStream())
            {
                byte request_data[] = PostBody.getBytes("UTF-8");
                os.write(request_data, 0, request_data.length);
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            //응답 내용
            String line = null;
            line = in.readLine();
           // recv_user = line.substring(line.indexOf("dps_account_holder_name") + 26, line.indexOf("bank_tran_id") -3 );
            in.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public String GenTranId()
    {
        String tranId = "M202200556U";
        for(int i =0; i<9; i++)
        {
            int num = (int)(Math.random() * 10);
            tranId += Integer.toString(num);
        }
        return tranId;
    }
    public class Withdraw extends Thread
    {
        public void run()
        {

            Withdraw();
            System.out.println("#@@@@@@@@@@@@@@@");
        }
    }
}