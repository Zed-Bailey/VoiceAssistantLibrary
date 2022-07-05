package com.zed.VoiceAssistantLibrary;

import com.zed.VoiceAssistantLibrary.TextToIntent.TTI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Properties;

public abstract class Assistant {
    public static final Logger logger = LogManager.getLogger(Assistant.class);
    /**
     * The MaryTTS object
     */
    public TTS tts;

    /**
     * The text to intent object that handles command parsing
     */
    public TTI tti;


    /**
     * the mapping of intent to command
     */
    public Map<String, CommandInterface> intentMap;

    /**
     * Initialize any non-command classes/functions
     * @param prop the properties file
     */
    public abstract void Initialize(Properties prop);

    /**
     * run the application
     * @param prop
     */
    public abstract void Run(Properties prop);

    /**
     * Initialize the text to speech class
     * if this fails to initialize then the application will exit with ExitCode = 1
     * @return a text to speech object
     */
    public TTS InitializeTTS(AssistantVoice voice) {
        var tts = new TTS(voice.name);
        tts.Configure();
        logger.info("Initialized Text to speech with {}", voice);
        return tts;
    }

    /**
     * Initialize the text to grammer parser
     * @param grammarPath the path to the json grammer file
     * @return a tti object
     */
    public TTI InitializeTTI(String grammarPath) {
        return new TTI(grammarPath);
    }

    /**
     * Map the command intents from the grammar file to a class that extends the command interface
     * @param prop the properties file, allows you to pass keys/data to the command classes initializer
     * @return a Hashmap of intent -> command class, intent should match the intent in the grammar file
     */
    public abstract Map<String, CommandInterface> MapCommands(Properties prop);
}
