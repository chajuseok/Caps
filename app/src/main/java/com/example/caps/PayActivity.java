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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PayActivity extends AppCompatActivity {


    ImageButton chat;
    RecyclerView accountView;
    AccountAdapter AccountAdapter;
    List<Pay> accountList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        accountView = findViewById(R.id.accountView);

        AccountAdapter = new AccountAdapter(accountList, this);
        accountView.setAdapter(AccountAdapter);

        accountList.add(new Pay("국민 은행", "10000원"));
        accountList.add(new Pay("광주 은행", "10000원"));
        accountList.add(new Pay("신한 은행", "10000원"));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(accountView.getContext(), new LinearLayoutManager(this).getOrientation());
        accountView.addItemDecoration(dividerItemDecoration);


        AccountAdapter.setOnItemClickListener1(new AccountAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                //
                Toast.makeText(PayActivity.this, "ba", Toast.LENGTH_SHORT).show();
            }
        });
        AccountAdapter.setOnItemClickListener2(new AccountAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                //
                Toast.makeText(PayActivity.this, "ab", Toast.LENGTH_SHORT).show();

            }
        });

        Objects.requireNonNull(accountView.getLayoutManager())
                .scrollToPosition(accountList.size() - 1);

        RecyclerDecoration spaceDecoration = new RecyclerDecoration(30);
        accountView.addItemDecoration(spaceDecoration);

        chat = findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PayActivity.this, MainActivity3.class);
                PayActivity.this.startActivity(intent);
            }
        });


    }


}


