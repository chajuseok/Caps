package com.example.caps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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
import java.util.concurrent.Executor;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthActivity extends AppCompatActivity {
    String access_token = null;
    String user_seq_no = null;
    String id = null;
    String password = null;
    private FirebaseAuth mFirebaseAuth; // 파이버베이스 인증
    private DatabaseReference mDatabaseRef; // 서버와 연동 실시간 db
    boolean fingerPrint = false;
    FirebaseUser firebaseUser;
    UserAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        setSupportActionBar(findViewById(R.id.toolbar));
        String android_id = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        Log.d("boolean" , "" + fingerPrint);


        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("DB");

        EditText idText = (EditText) findViewById(R.id.idText);
        EditText passwordText = (EditText) findViewById(R.id.passwordText);


        Button registerButton = (Button) findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //회원가입처리
                id = idText.getText().toString();
                password = passwordText.getText().toString();

                //firebase auth 진행
                mFirebaseAuth.createUserWithEmailAndPassword(id, password).addOnCompleteListener(AuthActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            firebaseUser = mFirebaseAuth.getCurrentUser();
                            account = new UserAccount();
                            account.setIdToken(firebaseUser.getUid()); //고유값
                            account.setEmailId(firebaseUser.getEmail());
                            account.setPassword(password);
                            account.setAndroidId(android_id);
                            //setValue : database에 insert
                            Network th = new Network();
                            th.start();
                            try {
                                th.join();
                            }catch(InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                            account.setAccessToken(access_token);
                            account.setUserSeqNo(user_seq_no);
                            Toast.makeText(AuthActivity.this, "회원가입에 성공하셨습니다", Toast.LENGTH_SHORT).show();
                            fingerPrintAlert();

                        }
                        else{
                            Toast.makeText(AuthActivity.this, "회원가입에 실패하셨습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                // 액티비티 이동\
                Intent intent =new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
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
            AssetManager am = getResources().getAssets();
            OutputStream out = serv_sock.getOutputStream();
            InputStream in = serv_sock.getInputStream();
            InputStream a = am.open("bank.html", AssetManager.ACCESS_BUFFER);
            BufferedReader reader = new BufferedReader(new InputStreamReader(a));
            in.read(recvBuffer);
            response = new String(recvBuffer);
            String html = "";
            String line = "";
            while((line=reader.readLine()) != null)
            {
                html += line;
            }
            out = serv_sock.getOutputStream();
            out.write(new String ("HTTP/1.1 200 OK\r\n").getBytes());
            out.write(new String ("Content-Length:"+html.getBytes().length+"\r\n").getBytes());
            out.write(new String ("Content-Type: text/html;charset=UTF-8\r\n\r\n").getBytes());
            out.write(html.getBytes());
            out.flush();
            out.close();
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
            user_seq_no = GetUserSeqNo(token);
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
    public String GetUserSeqNo(String message)
    {
        String seqNo = null;

        if(message.indexOf("user_seq_no") >= 0)
        {
            seqNo = message.substring(message.indexOf("user_seq_no")+14,message.indexOf("user_seq_no")+24);
        }
        else
        {
            seqNo = "Error";
        }
        return seqNo;
    }
    public class Network extends Thread
    {
        public void run()
        {
            Auth();
            access_token = GetTok(GetCode());
        }
    }
    public void fingerPrintAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(AuthActivity.this);
        builder.setTitle("알림");
        builder.setMessage("로그인을 위해 지문을 사용하시겠어요?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fingerPrintAuth();
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                AuthActivity.this.startActivity(intent);
                finish();
            }
        });
        builder.setNeutralButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                AuthActivity.this.startActivity(intent);
                finish();
            }
        });
        builder.create().show(); //보이기
    }

    public void fingerPrintAuth()
    {
        Executor executor;
        BiometricPrompt biometricPrompt;
        BiometricPrompt.PromptInfo promptInfo;

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(AuthActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "기기에 먼저 지문을 등록해주세요" + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "지문등록 완료", Toast.LENGTH_SHORT).show();
                fingerPrint = true;
                account.setfingerPrint(fingerPrint);
                mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);
                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                AuthActivity.this.startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "지문인증이 실패했습니다",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("본인 인증")
                .setNegativeButtonText("취소")
                .build();
        biometricPrompt.authenticate(promptInfo);
    }


}