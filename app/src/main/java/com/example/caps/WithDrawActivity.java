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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WithDrawActivity extends AppCompatActivity {

    Spinner spinner;
    String bank_number; // 계좌번호
    String bank_id;
    String money;
    ImageButton ocr;
    Button withdraw;
    Bitmap bitmap;
    EditText bank;
    EditText moneye;
    String fintech_use_num;
    String access_token;
    private static final int REQUEST_CAMERA_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_draw);
        ocr = findViewById(R.id.ocr);
        withdraw = findViewById(R.id.withdraw);
        Intent intent = getIntent();
        fintech_use_num = intent.getStringExtra("fintech_use_num");
        access_token = intent.getStringExtra("access_token");
        bank = findViewById(R.id.bank_number);
        moneye= findViewById(R.id.money);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김


        spinner = findViewById(R.id.spinner);

        ArrayAdapter monthAdapter = ArrayAdapter.createFromResource(this, R.array.my_array, R.layout.spinner_item);
        //R.array.test는 저희가 정의해놓은 1월~12월 / android.R.layout.simple_spinner_dropdown_item은 기본으로 제공해주는 형식입니다.
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(monthAdapter); //어댑터에 연결해줍니다.


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bank_id = spinner.getSelectedItem().toString();


            } //이 오버라이드 메소드에서 position은 몇번째 값이 클릭됬는지 알 수 있습니다.
            //getItemAtPosition(position)를 통해서 해당 값을 받아올수있습니다.

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (ContextCompat.checkSelfPermission(WithDrawActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WithDrawActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, REQUEST_CAMERA_CODE);
        }

        ocr.setOnClickListener(new View.OnClickListener() { //ocr이벤트처리
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
                    getTextFromImage(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getTextFromImage(Bitmap bitmap){
        TextRecognizer recognizer = new TextRecognizer.Builder(this).build();
        if(!recognizer.isOperational()){
            Toast.makeText(WithDrawActivity.this, "Error Occurred!!!",Toast.LENGTH_SHORT ).show();
        }
        else{
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlockSparseArray = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for(int i =0; i<textBlockSparseArray.size();i++){

                TextBlock textBlock = textBlockSparseArray.valueAt(i);
                stringBuilder.append(textBlock.getValue());
               // stringBuilder.append("\n");
            }
            bank_number = stringBuilder.toString(); //계좌번호인식
            bank.setText(bank_number.replaceAll(" ", ""));

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
            while((line = in.readLine()) != null)
            {
                System.out.println(line);
            }
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


}