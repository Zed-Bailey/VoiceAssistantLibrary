# Voice assistant Library
An offline privacy first voice assistant library

## Example
An example implementation of a simple voice assistant can be found [here](https://github.com/Zed-Bailey/VoiceAssistantExample)

## Building library
To build your own jar use `./gradlew shadowJar`. The jar will be output in `build/libs/` you can then import it into your project 

## Implementing your own voice assistant
To implement your own assistant, extended the abstract class `VoiceAssistant.Assistant` and implement the methods

As a base you can [clone the example](https://github.com/Zed-Bailey/VoiceAssistantExample) and build on top of that
The initialize function should call `Assistant.InitializeTTI` and `Assistant.InitializeTTS` and `MapCommands`
```java
// example/ExampleAssistant.java
public class ExampleAssistant extends Assistant {
    
    @Override
    public void Initialize(Properties prop) {
        this.tti = this.InitializeTTI(prop.getProperty("commandJson"));
        this.tts = this.InitializeTTS(AssistantVoice.poppy);
        this.intentMap = this.MapCommands(prop);

        // initialize anything else after
        // ....
    }
}
```
Without initializing these the assistant won't function.

Implementing a new command should extend `CommandInterface` this defines a single method `Execute` that you 
overwrite with your command functionality.

You should handle any possible failures in the function and return a string containing what you want spoken.
An example implementation is in `example/WeatherCommand.java`

## Handling Intents and Commands
When the intent callback is called an `Intent` object is passed through. If this is null then the spoken text could either not be understood
or there was no matching grammar in the grammar's json file.

to get the command class that was associated with the intent, call the intentMap 
```java
var command = this.intentMap.getOrDefault(intent.intent, null);
```

The slots map contains the variable, and it's value as defined in the grammar json file
```json
{
    "name": "getWeather",
    "speech" : [
      "whats the weather in $*:location",
      ....
    ],
    "slots" : {}
}
```
if the above grammar was defined and the command "whats the weather in london" was spoken 
the slots map would contain an entry with the key = "location" and value = "london"


intent handling method example:
```java
// from example: https://github.com/Zed-Bailey/VoiceAssistantExample/blob/master/src/main/java/Example/Main.java 
public void HandleIntent(Intent intent) {
        // if the command couldn't be parsed then intent is null
        if(intent == null) {
            this.tts.Speak("Sorry i didn't quite understand");
            return;
        }

        // check for mapping
        var command = this.intentMap.getOrDefault(intent.intent, null);
        if(command == null) {
            this.tts.Speak("Sorry no mapping for that command");
            return;
        }

        // execute command and speak the returned string
        this.tts.Speak(command.ExecuteCommand(intent));
}

```

## Defining Commands

An example command can be found in the `example/` directory

creating your own command
example weather grammar
```json
{
    "name": "getWeather",
    "speech" : [
      "whats the weather in $*:location",
      "weather in $*:location",
      "get weather in $*:location",
      "get the weather in $*:location"
    ],
    "slots" : {}
}
```
`$*:location` is a wildcard slot and will return whatever value is in that position
"weather in melbourne" will return the intent `{intent: getWeather, slots {location = "melbourne"}}`
if you want to match against a set list of values you can use slots
```json
{
    "name": "musicControl",
    "speech" : [
      "$controlaction:action music",
      "$controlaction:action song"
    ],
    "slots" : {
      "controlaction" : ["pause", "play", "skip", "rewind", "shuffle"]
    }
  }
```
here `$controlaction:action` will map the spoken value to one of the possible values in the controlaction array
"play music" will return an intent `{intent: musicControl, slots {action = "play"}}`

to return the rest of the sentence `$>:variable` can be used
```json
{
    "name": "google",
    "speech" : [
      "google $>:search"
    ],
    "slots" : {}
  }
```
"google how many kilos in a pound" will return an intent `{intent: google, slots {search = "how many kilos in a pound"}}`

## Pre-Installed voices
- poppy
- prudence
- spike
- obidiah

voices were installed with the marytts gui installer (installed with the marytts server)
then copied from their install location to this projects `lib/voices` folder


## Speech to text model
This library uses vosk for speech to text.
1. download model from [here](https://alphacephei.com/vosk/models) and unzip it
2. pass in the directory name to the `SpeechListener` object when you call it's `Start` method
```java
//.....
var listener = new SpeechListener("wakeword", ttiObject)
        .setWakeWordCallBack(/* callback here */)
        .setIntentCallBack(/* callback here */);
listener.Start("path/to/unzipped-vosk-model-dir")
//.....

```



## Custom voice
experiment with a custom voice?
https://github.com/marytts/gradle-marytts-voicebuilding-plugin
