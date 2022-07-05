package com.zed.VoiceAssistantLibrary;

import marytts.LocalMaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Objects;

public class TTS {
    private static final Logger logger = LogManager.getLogger(TTS.class);
    private final String voiceName;

    // buffer size for the audio streaming
    private static final int BUFFER_SIZE = 4096;
    private LocalMaryInterface mary;

    public TTS(String voice) {
        this.voiceName = voice;
    }

    public void Configure() {
        try {
            mary = new LocalMaryInterface();
            mary.setVoice(this.voiceName);
        } catch (MaryConfigurationException e) {
            logger.fatal("Could not initialize MaryTTS interface: ", e);
            System.exit(1);
        }
    }

    //TODO: Handle thrown exception
    public void Speak(String inputText)  {
        if (Objects.equals(inputText, "") || inputText == null) {
            return;
        }

        try {
            // synthesize
            AudioInputStream audio = mary.generateAudio(inputText);


            // this gist was the basis of the audio playing
            // we are using a buffer as we don't know how long the generated audio clip will be
            // https://gist.github.com/aw1231/1370045/e39926c6e04957b0130ddef3a7a55a978698ab84
            AudioFormat audioFormat = audio.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

            // play the generated audio
            byte[] bufferBytes = new byte[BUFFER_SIZE];
            int readBytes = -1;
            while ((readBytes = audio.read(bufferBytes)) != -1) {
                sourceDataLine.write(bufferBytes, 0, readBytes);
            }
            sourceDataLine.drain();
            sourceDataLine.close();
            audio.close();
        } catch (SynthesisException | LineUnavailableException | IOException e) {
            logger.fatal("Exception occurred while trying to synthesize text \"{}\". exception: {}", inputText, e);
            System.exit(1);
        }

    }


}