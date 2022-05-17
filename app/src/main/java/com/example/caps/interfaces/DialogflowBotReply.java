package com.example.caps.interfaces;

import com.google.cloud.dialogflow.v2.DetectIntentResponse;

public interface DialogflowBotReply {
    void callback(DetectIntentResponse returnResponse);
}
