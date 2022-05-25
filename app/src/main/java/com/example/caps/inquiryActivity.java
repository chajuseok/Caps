package com.example.caps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;
import android.content.Intent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

public class inquiryActivity extends AppCompatActivity {

    TextView restMoney; // 잔액
    RecyclerView inquiryView;

    InquiryAdapter inquiryAdapter;
    List<InquiryData> InquiryList = new ArrayList<>();
    String access_token;
    String fintech_use_num;
    Vector<String> tran_amt = new Vector<String>();
    Vector<String> content = new Vector<String>();
    Vector<String> inout_type = new Vector<String>();
    String rest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김


        restMoney = findViewById(R.id.restMoney);
        inquiryView = findViewById(R.id.inquiryView);

        inquiryAdapter = new InquiryAdapter(InquiryList, this);
        inquiryView.setAdapter(inquiryAdapter);
        Intent intent = getIntent();
        access_token = intent.getStringExtra("access_token");
        fintech_use_num = intent.getStringExtra("fintech_use_num");

        SavingInq saving = new SavingInq();
        saving.start();
        try {
            saving.join();
        }catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        restMoney.setText("잔액 : " + rest);
    }


    public void SavingInq()
    {
        String address = "https://testapi.openbanking.or.kr/v2.0/account/transaction_list/fin_num?";
        Properties parmas = new Properties();
        parmas.setProperty("bank_tran_id", GenTranId());
        parmas.setProperty("fintech_use_num", fintech_use_num);
        parmas.setProperty("inquiry_type", "A");
        parmas.setProperty("inquiry_base", "D");
        parmas.setProperty("from_date", "20190101");
        parmas.setProperty("to_date", "20220526");
        parmas.setProperty("sort_order", "D");
        parmas.setProperty("tran_dtime", "20220328103410");


        try
        {
            URL url = new URL(address+encodedString(parmas));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization",access_token );
            connection.setDoInput(true);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            //응답 내용
            String line = in.readLine();
            rest = line.substring(line.indexOf("balance_amt")+14,line.indexOf("page_record_cnt")-3);
            line = line.substring(line.indexOf("res_list"), line.length()-1);

            //int base = 0;
            while(line.indexOf("inout_type") >= 0)
            {
                inout_type.add(line.substring(line.indexOf("inout_type")+13,line.indexOf("tran_type")-3));
                content.add(line.substring(line.indexOf("print_content")+16, line.indexOf("tran_amt")-3));
                tran_amt.add(line.substring(line.indexOf("tran_amt")+11, line.indexOf("after_balance_amt")-3));
                //base = line.indexOf("account_holder_type", base) + 20;
                line = line.substring(line.indexOf("branch_name") + 2, line.length());
            }
            in.close();

            for(int i = 0; i<inout_type.size(); i++){
                InquiryList.add(new InquiryData(inout_type.get(i), content.get(i), tran_amt.get(i)));
            }


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
    public class SavingInq extends Thread
    {
        public void run()
        {
            SavingInq();
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

}