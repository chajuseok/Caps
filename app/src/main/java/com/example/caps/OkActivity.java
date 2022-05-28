package com.example.caps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OkActivity extends AppCompatActivity {


    TextView moneyText;
    TextView nameText;
    Button finishButton;
    String money;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok);
        moneyText = findViewById(R.id.moneyText);
        nameText = findViewById(R.id.nameText);
        finishButton = findViewById(R.id.okButton);


        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김


        Intent intent = getIntent();
        money = intent.getStringExtra("money"); // 보낸 금액
        name = intent.getStringExtra("name"); // 받는 분

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OkActivity.this, PayActivity.class);
                OkActivity.this.startActivity(intent);
                finish();
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

}