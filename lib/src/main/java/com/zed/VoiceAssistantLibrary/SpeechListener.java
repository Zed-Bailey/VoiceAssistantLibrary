package com.zed.VoiceAssistantLibrary;


import com.zed.VoiceAssistantLibrary.CallBacks.IntentCallBack;
import com.zed.VoiceAssistantLibrary.CallBacks.WakeWordCallBack;
import com.zed.VoiceAssistantLibrary.TextToIntent.TTI;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;


//TODO: This class needs a better name! CommandListener?
public class SpeechListener {

    private final String wakeWord;
    private WakeWordCallBack wakeWordCallBack;
    private IntentCallBack intentCallBack;
    private final TTI _tti;

    /**
     * Create a new Listener object
     * @param wakeWord the wake word to listen for
     * @param tti a text to intent object initialized with the grammar file
     */
    public SpeechListener(String wakeWord, TTI tti) {
        this.wakeWord = wakeWord;
        this._tti = tti;
    }

    /**
     * Sets the wake word callback function
     * @param w the callback
     * @return returns itself modified
     */
    public SpeechListener setWakeWordCallback(WakeWordCallBack w) {
        this.wakeWordCallBack = w;
        return this;
    }

    /**
     * Set the intent callback. called once the command has been parsed
     * @param i the callback
     * @return returns itself modified
     */
    public SpeechListener setIntentCallBack(IntentCallBack i) {
        this.intentCallBack = i;
        return this;
    }

    /**
     * Start listening through the microphone for commands
     * @param modelDirPath the path to the vosk model directory
     * @throws IOException
     */
    public void Start(String modelDirPath) throws IOException {
        LibVosk.setLogLevel(LogLevel.DEBUG);

        // answer to this question is the basis of the vosk microphone listening
        // https://stackoverflow.com/questions/68401284/use-the-microphone-in-java-for-speech-recognition-with-vosk

        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 60000, 16, 2, 4, 44100, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        TargetDataLine microphone;
        try (Model model = new Model(modelDirPath);
             Recognizer recognizer = new Recognizer(model, 120000)) {
            try {

                recognizer.setMaxAlternatives(1);

                microphone = (TargetDataLine) AudioSystem.getLine(info);
                microphone.open(format);
                microphone.start();

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int numBytesRead;
                int CHUNK_SIZE = 1024;
                System.out.println("[INFO] Now listening");

                byte[] b = new byte[4096];
                boolean shutdown = false;
                boolean awoken = false;

                while (!shutdown) {
                    numBytesRead = microphone.read(b, 0, CHUNK_SIZE);

                    out.write(b, 0, numBytesRead);

                    if (recognizer.acceptWaveForm(b, numBytesRead)) {
                        var input = recognizer.getFinalResult();
                        // parse the json string that vosk outputs
                        Any any = JsonIterator.deserialize(input);
                        var stt = any.get("alternatives", 0, "text").toString();
                        if (stt.contains(wakeWord)) {
                            // remove wake word from command
                            stt = stt.replace(wakeWord, "").trim();

                            // parse intent
                            var intent = _tti.ParseTextToCommand(stt);

                            // reset wake word status
                            awoken = false;

                            this.intentCallBack.intentParsed(intent);
                        }


                    } else {
                        var partial = JsonIterator.deserialize(recognizer.getPartialResult()).get("partial").toString();
                        // check if the partial word matches the wake word, if it does
                        if (Objects.equals(partial, wakeWord) && !awoken) {
                            awoken = true;
                            this.wakeWordCallBack.wakeWordDetected();
                        }
                    }
                }

                microphone.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
