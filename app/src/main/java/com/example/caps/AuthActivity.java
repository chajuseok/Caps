package com.example.caps;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Enumeration;
import java.util.Properties;
import java.net.URLEncoder;
import android.content.Intent;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;


public class AuthActivity extends AppCompatActivity {
    static String access_token = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김


        Button sign_up = (Button) findViewById(R.id.registerButton);


        // 회원가입버튼 이벤트 처리
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth();

                Network th = new Network();
                th.start();
                try {
                    th.join();
                }catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(AuthActivity.this);
                builder.setTitle("토큰"); //AlertDialog의 제목 부분
                builder.setMessage(access_token); //AlertDialog의 내용 부분
                builder.setPositiveButton("예",null);
                builder.setNegativeButton("아니오",  null);
                builder.setNeutralButton("취소", null);
                builder.create().show(); //보이기
            }
        });

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                // 액티비티 이동
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void Auth()
    {
        String address = "https://testapi.openbanking.or.kr/oauth/2.0/authorize?";
        Properties parmas = new Properties();
        parmas.setProperty("response_type", "code");
        parmas.setProperty("client_id", "1eaec044-0b78-41fe-99cd-a6afad2cdeba");
        parmas.setProperty("redirect_uri", "http://localhost:8000");
        parmas.setProperty("scope", "login inquiry transfer");
        parmas.setProperty("state", "b80BLsfigm9OokPTjy03elbJqRHOfGSY");
        parmas.setProperty("auth_type", "0");

        String url =address+encodedString(parmas);
        call(url);
    }
    public String encodedString(Properties params)
    {
        StringBuffer sb = new StringBuffer(256);
        Enumeration keys = params.propertyNames();
        try
        {
            while(keys.hasMoreElements())
            {
                String key = (String) keys.nextElement();
                String value = params.getProperty(key);
                sb.append(URLEncoder.encode(key, "UTF-8")+"="+URLEncoder.encode(value, "UTF-8"));

                if(keys.hasMoreElements())
                    sb.append("&");
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }
    public void call(String url)
    {
        Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browser);
    }
    public String GetCode()
    {
        int maxBufferSize = 1024;
        byte[] recvBuffer = new byte[maxBufferSize];
        String response = null;
        try
        {
            ServerSocket listener = new ServerSocket(8000);
            Socket serv_sock = listener.accept();
            InputStream in = serv_sock.getInputStream();
            in.read(recvBuffer);
            response = new String(recvBuffer);
            listener.close();
            serv_sock.close();
            if(response.indexOf("code") >= 0)
            {
                response =  response.substring(response.indexOf("code")+5,response.indexOf("&"));
            }
            else
            {
                response = "Error";
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return response;
    }

    public String GetTok(String code)
    {
        String address = "https://testapi.openbanking.or.kr/oauth/2.0/token";
        Properties params = new Properties();
        params.setProperty("code", code);
        params.setProperty("client_id", "1eaec044-0b78-41fe-99cd-a6afad2cdeba");
        params.setProperty("client_secret", "2482daed-0be3-40e2-9b7d-4ad9c632e3e6");
        params.setProperty("redirect_uri","http://localhost:8000");
        params.setProperty("grant_type", "authorization_code");
        String param = encodedString(params);
        String token = null;

        try
        {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            try(OutputStream os = connection.getOutputStream())
            {
                os.write(param.getBytes());
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            token = in.readLine();
            in.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        if(token.indexOf("access_token") >= 0)
        {
            token =  token.substring(token.indexOf("access_token")+15, token.indexOf(",") -1);
        }
        else
        {
            token = "Error";
        }
        return "Bearer" + token;
    }
    public class Network extends Thread
    {
        public void run()
        {
            access_token = GetTok(GetCode());
        }
    }
}