package com.zed.VoiceAssistantLibrary.TextToIntent;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A text to intent parser
 */
public class TTI {

    private final Command[] commands;
    private static final Logger logger = LogManager.getLogger(TTI.class);

    /**
     *
     * @param intentFilePath The file path to the intent definitions that will be matched to the speech
     */
    public TTI(String intentFilePath) {
        commands = LoadAndParse(intentFilePath);
    }

    /**
     * Parse the text to an intent
     * @param text the spoken text
     * @return an Intent object. will return null if the intent is unknown or couldn't be parsed
     */
    public Intent ParseTextToCommand(String text) {
        for (Command c : commands) {
            var match = c.Match(text);
            if(match != null)
                return match;
        }
        return null;
    }

    /**
     * Load the json grammar file and parse it into the appropriate commands
     * @param path path to the file
     * @return an array of commands
     */
    private Command[] LoadAndParse(String path)  {

        var filePath = Path.of(path);
        String content = null;
        try {
            content = Files.readString(filePath);
        }catch (IOException e) {
            logger.fatal(e);
            System.exit(1);
        }

        if(content == null) {
            logger.fatal("Failed to load json command file: " + path);
            System.exit(1);
        }

        Gson gson = new Gson();
        var parsedCommands = gson.fromJson(content, Command[].class);

        for (var command : parsedCommands) {
            PrintCommand(command);
        }

        return parsedCommands;
    }

    /**
     * Pretty prints a command to the console
     * @param c the command to print
     */
    private void PrintCommand(Command c) {
        System.out.printf("loaded command:\n\tname: %s\n", c.name);
        System.out.printf("\tspeech: %s\n", c.speech);
        System.out.println("\tslots:");
        c.slots.forEach((s, strings) -> System.out.printf("\t\t%s : %s\n", s, strings));
    }



    /*

    wildcard slots $*:output
    wildcard slots will simply return whatever word is in that position

    intent = the intent of the command, e.g. musicControl or getWeather
    speech = array of possible alternatives sayings for this intent
    each speech string can have optional sayings in ()
    each speech saying can also have alternative words in []
    e.g
    "turn on [all the] lights" will evaluate to "turn on lights" and "turn on all the lights"
     and will return `{ "intent" : "lightControl" }`

    wildcard slots
    "get weather in $*:location"
    this will simply return the text at that location e.g.
    "get weather in melbourne" will return
    {"intent": "getWeather", "slots" : {"location": "melbourne"}}
    alternatively had I said something else instead of melbourne then that would've been returned

     */
}
