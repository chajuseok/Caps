package com.example.caps;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class WithDrawActivity extends AppCompatActivity {

    final String boundary = "*******";
    final String crlf = "\r\n";
    final String twoHyphens = "--";

    ArrayList<String> list = new ArrayList<>(); // 타입 생략 가능


    String bank_number = ""; // 계좌번호
    String bank_id = "";
    String money;
    ImageButton ocrButton;
    Button withdraw;
    Bitmap bitmap;
    EditText bank_name; //은행명
    EditText bank; //계좌번호
    EditText moneye;
    String fintech_use_num;
    String access_token;
    String recv_user;
    String bank_info = "";
    String id;
    private static final int REQUEST_CAMERA_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_draw);
        bank_name = findViewById(R.id.bank_name);
        ocrButton = findViewById(R.id.ocr);
        withdraw = findViewById(R.id.withdraw);
        Intent intent = getIntent();
        fintech_use_num = intent.getStringExtra("fintech_use_num");
        access_token = intent.getStringExtra("access_token");
        id = intent.getStringExtra("id");
        bank = findViewById(R.id.bank_number);
        moneye= findViewById(R.id.money);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김


        if (ContextCompat.checkSelfPermission(WithDrawActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WithDrawActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE);
        }

        ocrButton.setOnClickListener(new View.OnClickListener() { //ocr이벤트처리
            @Override
            public void onClick(View view) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(WithDrawActivity.this);
            }
        });

        withdraw.setOnClickListener(new View.OnClickListener() {  // 송금이벤트처리
            @Override
            public void onClick(View view) {
                money = moneye.getText().toString();
                bank_number = bank.getText().toString();
                Withdraw th = new Withdraw();
                th.start();
                try {
                    th.join();
                }catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
                Intent intent = new Intent(WithDrawActivity.this, OkActivity.class);
                intent.putExtra("money", money);
                intent.putExtra("id", id);
                intent.putExtra("recv_user", recv_user);
                WithDrawActivity.this.startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),resultUri);
                    Ocr ocr = new Ocr(bitmap);
                    ocr.start();
                    try {
                        ocr.join();
                    }catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    for(int i = 0; i<list.size(); i++)
                    {
                        Log.d("OCR 인식결과", ""+ list.get(i));
                    }
                    for(int i = 0; i<list.size(); i++)
                    {
                        bank_info += list.get(i);
                    }
                    Log.d("bank_info" , "" + bank_info);
                    for(int i=0; i<bank_info.length(); i++)
                    {
                        char temp = bank_info.charAt(i);
                        if(temp >= '0' && temp <= '9')
                        {
                            bank_number += temp;
                        }
                        else if(temp == '-' || temp == ' ')
                        {
                            continue;
                        }
                        else
                        {
                            bank_id += temp;
                        }
                    }
                    Log.d("bank_number", "" + bank_number);
                    Log.d("bank_id" , "" + bank_id);

                    bank_name.setText(bank_id);
                    bank.setText(bank_number);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
            recv_user = line.substring(line.indexOf("dps_account_holder_name") + 26, line.indexOf("bank_tran_id") -3 );
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
        }
    }

    public void kakao(Bitmap bit) {

        String line = null;

        try {

            //image = BitmapFactory.decodeResource(getResources(), R.drawable.a);
            byte[] byteArray = bitmapToByteArray(bit);
            URL url = new URL("https://dapi.kakao.com/v2/vision/text/ocr");
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Authorization", "KakaoAK 6930a3c7b2d5fe8fc46218cc15bc3e89");
            httpConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + this.boundary);


            OutputStream httpConnOutputStream = httpConn.getOutputStream();
            DataOutputStream request = new DataOutputStream(httpConnOutputStream);


            request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\""+ "a.png"+"\"" + this.crlf);
            request.writeBytes(this.crlf);
            request.write(byteArray);
            request.writeBytes(this.crlf);
            request.writeBytes(this.twoHyphens + this.boundary +
                    this.twoHyphens + this.crlf);
            request.flush();
            request.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            line = in.readLine();
            in.close();
            System.out.println((String)line);
            JSONObject obj = new JSONObject(line);
            JSONArray a = (JSONArray) obj.get("result");
            for(int i =0; i<a.length(); i++) {

                JSONObject jsonObject = (JSONObject)a.get(i);
                JSONArray name = (JSONArray)jsonObject.get("recognition_words");
                list.add((String)name.get(0));
            }


        }catch(Exception e)
        {
            e.printStackTrace();
        }



    }

    public class Ocr extends Thread
    {
        Bitmap bit;
        public Ocr(Bitmap bit){
            this.bit = bit;
        }
        public void run()
        {
            kakao(bit);
        }
    }

    public byte[] bitmapToByteArray( Bitmap bitmap ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        bitmap.compress( Bitmap.CompressFormat.PNG, 90, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        return byteArray ;
    }
}