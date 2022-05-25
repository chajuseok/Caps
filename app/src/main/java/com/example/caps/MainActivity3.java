package com.example.caps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caps.interfaces.DialogflowBotReply;
import com.example.caps.utils.SendMessageInBg;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.common.collect.Lists;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MainActivity3 extends AppCompatActivity implements DialogflowBotReply {

    RecyclerView chatView;
    Adapter chatAdapter;
    List<MessageData> messageList = new ArrayList<>();
    EditText editMessage;
    ImageButton btnSend;
    Button backButton;
    ImageButton helpbtn;
    //계좌내역을 위해
    String access_token;
    String fintech_use_num;

    //dialogFlow
    private SessionsClient sessionsClient; // 세션 클라이언트
    private SessionName sessionName; //세션 이름
    private String uuid = UUID.randomUUID().toString(); //식별자
    private String TAG = "MainActivity3"; //Tag 명

    //stt
    final int PERMISSION = 1;
    SpeechRecognizer mRecognizer;

    DrawerLayout drawerLayout;
    View drawerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        Intent intent = getIntent();
        access_token = intent.getStringExtra("access_token");
        fintech_use_num = intent.getStringExtra("fintech_use_num");

        chatView = findViewById(R.id.chatView);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);
        backButton = findViewById(R.id.backButton);
        helpbtn = findViewById(R.id.helpButton);


        chatAdapter = new Adapter(messageList, this);
        chatView.setAdapter(chatAdapter);

        messageList.add(new MessageData("안녕하세요 금융도우미입니다. \n 무엇을 도와드릴까요?", true));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent a = new Intent(MainActivity3.this, LoginActivity.class);
                //MainActivity3.this.startActivity(a);
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = editMessage.getText().toString();
                System.out.println(message);
                if (!message.isEmpty()) {
                    messageList.add(new MessageData(message, false));
                    editMessage.setText("");
                    sendMessageToBot(message);
                    Objects.requireNonNull(chatView.getAdapter()).notifyDataSetChanged();
                    Objects.requireNonNull(chatView.getLayoutManager())
                            .scrollToPosition(messageList.size() - 1);
                } else {
                    Toast.makeText(MainActivity3.this, "입력해주세요!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        setUpBot();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //     drawerView = (View) findViewById(R.id.drawerView);
        //   drawerLayout.addDrawerListener(listener);
/*
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(drawerView);
            }
        });


 */

        helpbtn = findViewById(R.id.helpButton);
        helpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity3.this);
//                builder.setTitle("필요하신 기능을 입력하면 \n 화면으로 이동합니다. \n")
//                        .setMessage("송금 \n" +
//                                "계좌조회 \n" +
//                                "거래내역 \n" )
//                        .setPositiveButton("확인", new DialogInterface.OnClickListener()
//                        {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i)
//                            {  }
//                        });
//
//                builder.show();

                Intent intentStt;
                intentStt = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intentStt.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
                intentStt.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
                if (Build.VERSION.SDK_INT >= 23) {
                    ActivityCompat.requestPermissions(MainActivity3.this, new String[]{Manifest.permission.INTERNET,
                            Manifest.permission.RECORD_AUDIO}, PERMISSION);
                }
                mRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity3.this);
                mRecognizer.setRecognitionListener(listener);
                mRecognizer.startListening(intentStt);

            }
        });

        DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        };
    }

    private void setUpBot () {
        try {
            InputStream stream = this.getResources().openRawResource(R.raw.credential);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
            String projectId = ((ServiceAccountCredentials) credentials).getProjectId();

            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(
                    FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            sessionName = SessionName.of(projectId, uuid);

            Log.d(TAG, "projectId : " + projectId);
        } catch (Exception e) {
            Log.d(TAG, "setUpBot: " + e.getMessage());
        }
    }

    //dialogflow로 message를 보내는 메서드
    private void sendMessageToBot (String message){
        QueryInput input = QueryInput.newBuilder()
                .setText(TextInput.newBuilder().setText(message).setLanguageCode("ko-US")).build();
        new SendMessageInBg(this, sessionName, sessionsClient, input).execute();
    }

    @Override
    public void callback (DetectIntentResponse returnResponse){
        //dialogflowAgent와 통신 성공한 경우
        if (returnResponse != null) {
            String dialogflowBotReply = returnResponse.getQueryResult().getFulfillmentText();
            Log.d("text", returnResponse.getQueryResult().toString());
            //getFulfillmentText가 있을 경우
            if (!dialogflowBotReply.isEmpty()) {
                //UI or something to do Task
                messageList.add(new MessageData(dialogflowBotReply, true));
                chatAdapter.notifyDataSetChanged();
                Objects.requireNonNull(chatView.getLayoutManager()).scrollToPosition(messageList.size() - 1);


                if(dialogflowBotReply.indexOf("송금") != -1){
                    Intent a = new Intent(MainActivity3.this, WithDrawActivity.class);
                    MainActivity3.this.startActivity(a);
                    finish();
                }
                if(dialogflowBotReply.indexOf("계좌") != -1  || dialogflowBotReply.indexOf("내역") != -1 ){
                    Intent intent = new Intent(MainActivity3.this, inquiryActivity.class);
                    intent.putExtra("access_token", access_token);
                    intent.putExtra("fintech_use_num", fintech_use_num);
                    MainActivity3.this.startActivity(intent);
                    finish();
                }
                if(dialogflowBotReply.indexOf("거래") != -1 ){
                    Intent intent = new Intent(MainActivity3.this, inquiryActivity.class);
                    intent.putExtra("access_token", access_token);
                    intent.putExtra("fintech_use_num", fintech_use_num);
                    MainActivity3.this.startActivity(intent);
                    finish();
                }
                if(dialogflowBotReply.indexOf("조회") != -1 || dialogflowBotReply.indexOf("잔액") != -1  ){
                    Intent intent = new Intent(MainActivity3.this, inquiryActivity.class);
                    intent.putExtra("access_token", access_token);
                    intent.putExtra("fintech_use_num", fintech_use_num);
                    MainActivity3.this.startActivity(intent);
                    finish();
                }



            } else {
                Toast.makeText(this, "다시 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "앱이 작동할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
    // STT
    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트워크 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간 초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }
            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다 : " +
                    message, Toast.LENGTH_SHORT).show();

        }


        @Override
        public void onResults(Bundle results) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for (int i = 0; i < matches.size(); i++) {
                editMessage.setText(matches.get(i));
            }

        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }

    };


}