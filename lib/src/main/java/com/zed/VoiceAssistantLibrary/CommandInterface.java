package com.zed.VoiceAssistantLibrary;


import com.zed.VoiceAssistantLibrary.TextToIntent.Intent;

/**
 * An interface that should be extended by a command class
 */
public interface CommandInterface {

    /**
     * This method should be overridden by the implementing class
     * this method will be called when the voice command is uttered
     * @return A string that will be synthesized and spoken
     */
    String ExecuteCommand(Intent intent);
}
