package com.zed.VoiceAssistantLibrary.CallBacks;

import com.zed.VoiceAssistantLibrary.TextToIntent.Intent;

public interface IntentCallBack {
    void intentParsed(Intent intent);
}
