package com.zed.VoiceAssistantLibrary.TextToIntent;


import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

// TODO: find a better name for this class
public class Command {
    public String name;
    public ArrayList<String> speech;
    public Map<String, ArrayList<String>> slots;


    /**
     * Match the text to a command phrase
     * @param text text from speech to text
     * @return null on no match, otherwise an intent
     */
    public Intent Match(String text) {
        for(String speech: speech) {
            var match = this.tryMatch(text, speech);
            if(match != null) {
                match.intent = this.name;
                return match;
            }
        }

        return null;
    }


    /**
     * Tries to match the passed in text with the command
     * @param spokenText the text to match
     * @param phrase the command phrase to match against
     * @return will return null if no match, otherwise wil return the intent
     */
    private Intent tryMatch(String spokenText, String phrase) {
        var spokenCommand = spokenText.split(" ");
        Intent parsedIntent = null;

        // split the phrase up
        var commandPhrase = phrase.split(" ");

        if (spokenCommand.length == commandPhrase.length) {
            parsedIntent = new Intent();
            for (int i = 0; i < spokenCommand.length; i++) {
                if(commandPhrase[i].contains("$")) {
                    // $controlaction:action -> ["controlaction", "action"]
                    var slot = (commandPhrase[i].replace("$", "")).split(":");

                    // handle wildcard slot
                    // simply assigns whatever is in the current position in the spoken text to the slot key value
                    if(Objects.equals(slot[0], "*")) {
                        parsedIntent.slots.put(slot[1], spokenCommand[i]);
                        continue;
                    }

                    // get the possible slot values for controlaction
                    var slotValues = this.slots.get(slot[0]);

                    // check that the spoken input has a matching input in the slots
                    var slotIndex = slotValues.indexOf(spokenCommand[i]);
                    if(slotIndex == -1){
                        return null;
                    }
                    // add the parsed slot to the value
                    parsedIntent.slots.put(slot[1], slotValues.get(slotIndex));
                }
                else if(!Objects.equals(spokenCommand[i], commandPhrase[i])) {
                    return null;
                }
            }
        } else if(commandPhrase[1].contains("$>:")) {
            // this will match commands the rest of the sentence variable
            // eg. google $>:search = "google how many kilos in a pound"
            parsedIntent = new Intent();
            var slot = (commandPhrase[1].replace("$", "")).split(":");
            // joins the sentence
            parsedIntent.slots.put(slot[1], String.join(" ", spokenCommand).replace(commandPhrase[0], "").trim());
        }

        return parsedIntent;
    }

}
