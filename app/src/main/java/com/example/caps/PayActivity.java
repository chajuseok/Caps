package com.example.caps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.auth.oauth2.AccessToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Vector;

public class PayActivity extends AppCompatActivity {


    ImageButton chat;
    RecyclerView accountView;
    AccountAdapter AccountAdapter;
    String user_seq_no;
    String access_token;
    Vector<String> fintechNum = new Vector<String>();
    Vector<String> bankName = new Vector<String>();
    Vector<String> accountNum = new Vector<String>();
    Vector<String> balanceAmt = new Vector<String>();
    List<Pay> accountList = new ArrayList<>();
    private DatabaseReference mDatabaseRef;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김

        Intent intent = getIntent();
        String id =  intent.getStringExtra("id");

        accountView = findViewById(R.id.accountView);
        AccountAdapter = new AccountAdapter(accountList, this);
        accountView.setAdapter(AccountAdapter);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("DB");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue().toString();
                int base = value.indexOf(id);
                user_seq_no = value.substring(value.indexOf(id) - 20,value.indexOf(id) -10);
                //access_token = "BearereyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiIxMTAxMDA1MzUwIiwic2NvcGUiOlsiaW5xdWlyeSIsImxvZ2luIiwidHJhbnNmZXIiXSwiaXNzIjoiaHR0cHM6Ly93d3cub3BlbmJhbmtpbmcub3Iua3IiLCJleHAiOjE2NjE0NDMzOTcsImp0aSI6IjNhYjYxNWFmLTUwMTMtNGEyNS1iNzg4LTRhM2RjZTMzYTdkZCJ9.-Nu4Jf_yX6F_CN4N73jlJ-4I1WAz8ZIfz-rRsOi1ckA";
                access_token = value.substring(value.indexOf(id) + id.length()+14, value.indexOf(id) + id.length()+ 313 );
                Log.d("user_seq_no", "" + user_seq_no);
                Log.d("access_token", "" + access_token);
                UserInq user = new UserInq();
                user.start();
                try {
                    user.join();
                }catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
                for(int i=0; i<fintechNum.size(); i++)
                {
                    Log.d("fin_tech", "" + fintechNum.get(i));
                    AccountInq account = new AccountInq(fintechNum.get(i));
                    account.start();
                    try {
                        account.join();
                    }catch(InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                }
                for(int i=0; i<balanceAmt.size(); i++)
                {
                    accountList.add(new Pay(bankName.get(i), balanceAmt.get(i)));
                }
                RecyclerDecoration spaceDecoration = new RecyclerDecoration(30);
                accountView.addItemDecoration(spaceDecoration);
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Database", "Failed to read value.", error.toException());
            }
        });


        AccountAdapter.setOnItemClickListener1(new AccountAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                //
                Intent intent = new Intent(PayActivity.this, inquiryActivity.class);
                intent.putExtra("access_token", access_token);
                intent.putExtra("fintech_use_num", fintechNum.get(pos));
                PayActivity.this.startActivity(intent);
            }
        });
        AccountAdapter.setOnItemClickListener2(new AccountAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {

                Intent intent = new Intent(PayActivity.this, WithDrawActivity.class);
                intent.putExtra("access_token", access_token);
                intent.putExtra("fintech_use_num", fintechNum.get(pos));
                PayActivity.this.startActivity(intent);
            }
        });

        Objects.requireNonNull(accountView.getLayoutManager())
                .scrollToPosition(accountList.size() - 1);


        chat = findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PayActivity.this, MainActivity3.class);
                intent.putExtra("access_token", access_token);
                intent.putExtra("fintech_use_num", fintechNum.get(0)); // 주계좌를 0번
                PayActivity.this.startActivity(intent);
            }
        });

    }

    public void UserInq()
    {
        String address = "https://testapi.openbanking.or.kr/v2.0/user/me?";
        Properties parmas = new Properties();
        int base = 0;
        String line = null;
        parmas.setProperty("user_seq_no", user_seq_no);

        try
        {
            URL url = new URL(address+encodedString(parmas));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", access_token);
            connection.setDoInput(true);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            line = in.readLine();
            line = line.replace("savings_bank_name", "");
            in.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        while(line.indexOf("fintech_use_num", base) >= 0)
        {
            fintechNum.add(line.substring(line.indexOf("fintech_use_num", base)+18,line.indexOf("fintech_use_num", base)+42));
            bankName.add(line.substring(line.indexOf("bank_name", base)+12, line.indexOf("account_num_masked", base)-3));
            accountNum.add(line.substring(line.indexOf("account_num_masked", base)+21, line.indexOf("account_holder_name", base)-3));
            base = line.indexOf("account_holder_type", base) + 20;
        }
    }
    public void AccountInq(String fintech_use_num)
    {
        String address = "https://testapi.openbanking.or.kr/v2.0/account/balance/fin_num?";
        Properties parmas = new Properties();
        String balance = null;
        String line = null;
        parmas.setProperty("bank_tran_id", GenTranId());
        parmas.setProperty("fintech_use_num", fintech_use_num);
        parmas.setProperty("tran_dtime", "20220328103410");

        try
        {
            URL url = new URL(address+encodedString(parmas));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization",access_token );
            connection.setDoInput(true);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            line = in.readLine();
            in.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        balance = line.substring(line.indexOf("balance_amt")+14, line.indexOf("available_amt")-3);
        balanceAmt.add(balance);
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
    public class UserInq extends Thread
    {
        public void run()
        {
            UserInq();
        }
    }
    public class AccountInq extends Thread
    {
        String fintech_use_num;

        AccountInq(String fin)
        {
            fintech_use_num = fin;
        }

        public void run()
        {
            AccountInq(fintech_use_num);
        }
    }
}