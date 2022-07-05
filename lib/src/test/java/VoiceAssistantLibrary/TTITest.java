package VoiceAssistantLibrary;

import com.zed.VoiceAssistantLibrary.TextToIntent.TTI;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TTITest {
    static TTI tti;

    @BeforeAll
    static void Init() {
        var path = Paths.get("src","test","resources", "example.json").toAbsolutePath().toString();
        tti = new TTI(path);
    }

    @Test
    void TestCommandIntent() {
        var match = tti.ParseTextToCommand("play music");
        assertNotNull(match);
        assertEquals("musicControl", match.intent);
        assertEquals("play", match.slots.get("action"));
    }

    @Test
    void TestCommandWithWildcard() {
        var match = tti.ParseTextToCommand("weather in melbourne");
        assertNotNull(match);
        assertEquals("getWeather", match.intent);
        assertEquals("melbourne", match.slots.get("location"));
    }

    @Test
    void TestCommandWithRestOfSentenceOperator() {
        var match = tti.ParseTextToCommand("google how many kilos in a pound");
        assertNotNull(match);
        assertEquals("google", match.intent);
        assertEquals("how many kilos in a pound", match.slots.get("search"));
    }
}
