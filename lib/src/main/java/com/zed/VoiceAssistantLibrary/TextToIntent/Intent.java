package com.zed.VoiceAssistantLibrary.TextToIntent;

import java.util.HashMap;

public class Intent {
    public String intent;
    public HashMap<String, String> slots = new HashMap<>();

    @Override
    public String toString() {
        return String.format("intent: %s\nslots: %s", this.intent, this.slots);
    }
}
