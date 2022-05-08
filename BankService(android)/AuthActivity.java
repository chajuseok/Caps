package com.example.caps;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import java.util.Enumeration;
import java.util.Properties;
import java.net.URLEncoder;
import android.content.Intent;


public class AuthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김


        Button sign_up = (Button) findViewById(R.id.sign_up);
        // 회원가입버튼 이벤트 처리
       sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth();
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
        Intent browser = new Intent(Intent.ACTION_VIEW,  Uri.parse(url));
        startActivity(browser);
    }
}

