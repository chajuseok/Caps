package com.example.caps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
    String value = null;
    String id = null;
    boolean fingerPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String android_id = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
        DatabaseReference mDatabaseRef;


        Button loginButton = (Button) findViewById(R.id.loginButton);
        ImageButton fingerPrintButton =(ImageButton)  findViewById(R.id.fingerPrint);
        Button registerButton = (Button) findViewById(R.id.registerButton);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("DB");
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                value = snapshot.getValue().toString();
                String fingerAccess;
                if(value.indexOf(android_id) >= 0)
                {
                    fingerAccess = value.substring(value.lastIndexOf('=',value.indexOf("androidId=" + android_id))+1, value.lastIndexOf(',',value.indexOf("androidId=" + android_id)));
                    id = value.substring(value.lastIndexOf("emailId=",value.indexOf("androidId=" + android_id)) +8 , value.lastIndexOf("accessToken",value.indexOf("androidId=" + android_id)) -2);
                    if(fingerAccess.equals("true"))
                        fingerPrint = true;
                    else
                        fingerPrint = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Database", "Failed to read value.", error.toException());
            }
        });

        Executor executor;
        BiometricPrompt biometricPrompt;
        BiometricPrompt.PromptInfo promptInfo;
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(MainActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "????????? ?????? ??????????????????" + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("??????");
                builder.setMessage(id + '\n'+ "????????? ????????? ????????? ?????????????");
                builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainActivity.this, PayActivity.class);
                        intent.putExtra("id",id);
                        MainActivity.this.startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("?????????",  null);
                builder.setNeutralButton("??????", null);
                builder.create().show(); //?????????
                Toast.makeText(getApplicationContext(),
                        "????????? ??????", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "????????? ??????",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("?????? ??????")
                .setNegativeButtonText("??????")
                .build();


        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent auth = new Intent(MainActivity.this, AuthActivity.class);
                MainActivity.this.startActivity(auth);
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent login = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(login);
                finish();
            }
        });

        fingerPrintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((value.indexOf(android_id) >= 0) && fingerPrint)
                {
                    biometricPrompt.authenticate(promptInfo);
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("??????");
                    builder.setMessage("????????? ????????? ????????? ????????????!");
                    builder.setNeutralButton("??????", null);
                    builder.create().show(); //?????????
                }

            }
        });
        
    }
}