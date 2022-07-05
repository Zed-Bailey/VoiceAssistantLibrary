package com.zed.VoiceAssistantLibrary;

/**
 * Default MaryTTS voices available to use for the text to speech
 */
public enum AssistantVoice {
    poppy("dfki-poppy-hsmm"),
    prudence("dfki-prudence-hsmm"),
    obadiah("dfki-obadiah-hsmm"),
    spike("dfki-spike-hsmm");

    public final String name;

    AssistantVoice(String name) {
        this.name = name;
    }

}
