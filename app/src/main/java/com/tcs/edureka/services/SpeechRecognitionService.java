package com.tcs.edureka.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.tcs.edureka.receivers.SpeechRecognitionReceiver;
import com.tcs.edureka.utility.Constants;
import com.tcs.edureka.utility.ConversionHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * @author Bhavya Bhanu
 */
public class SpeechRecognitionService extends Service implements RecognitionListener {
    private static final int DAY_INCREMENT_COUNT = 1;
    private static final int DATE_WITHOUT_YEAR_WORD_COUNT = 2;
    private Handler handler;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String patternBasicCommand;
    private Bundle bundle;

    public SpeechRecognitionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Set the pattern for identifying basic commands in speech (onCreate() will be called only once even if service is invoked multiple times)
        setPatternForBasicSpeechCommand();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Constants.TAG_SPEECH_RECOGNIZER, "Service onStartCommand");
        super.onStartCommand(intent, flags, startId);
        handler = getHandler();
        invokeSpeechRecognizer();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(Constants.TAG_SPEECH_RECOGNIZER, "Service destroy");
        speechRecognizer.stopListening();
        super.onDestroy();
    }


    public void invokeSpeechRecognizer() {
        Log.d(Constants.TAG_SPEECH_RECOGNIZER, "invokeSpeechRecognizer() start");
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(SpeechRecognitionService.this);
        if (SpeechRecognizer.isRecognitionAvailable(SpeechRecognitionService.this)) {
            speechRecognizer.setRecognitionListener(SpeechRecognitionService.this);
            speechRecognizerIntent = getSpeechRecognizerIntent();
            speechRecognizer.startListening(speechRecognizerIntent);
            Log.d(Constants.TAG_SPEECH_RECOGNIZER, "invokeSpeechRecognizer() start listening");
        } else {
            Log.d(Constants.TAG_SPEECH_RECOGNIZER, "invokeSpeechRecognizer() recognizer not available");
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(Constants.TAG_SPEECH_RECOGNIZER, "onReadyForSpeech() ");

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {
        Log.d(Constants.TAG_SPEECH_RECOGNIZER, "onError");

    }

    @Override
    public void onResults(Bundle results) {
        Log.d(Constants.TAG_SPEECH_RECOGNIZER, "Got user input. onResults start");
        if (results != null) {
            ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (data != null && !data.isEmpty()) {

                Log.d(Constants.TAG_SPEECH_RECOGNIZER, "onResults. User Input command - " + data.get(0));
                identifyCommand(data.get(0));

            }
        }

        //Start listening to speech again
        //speechRecognizer.startListening(speechRecognizerIntent);

    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }


    //------- Private methods ----------

    //Method used to set pattern for identifying basic command in the speech
    private void setPatternForBasicSpeechCommand() {
        ArrayList<String> basicCommands = new ArrayList<String>() {
            {
                add(Constants.SPEECH_RECOGNITION_COMMAND_APPOINTMENTS);
                add(Constants.SPEECH_RECOGNITION_COMMAND_CALL);
                add(Constants.SPEECH_RECOGNITION_COMMAND_CALL_HI);
                add(Constants.SPEECH_RECOGNITION_COMMAND_CALL_BYE);
                add(Constants.SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_PLAY);
                add(Constants.SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_NEXT);
                add(Constants.SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_PAUSE);
                add(Constants.SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_PREVIOUS);
                add(Constants.SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_STOP);
                add(Constants.SPEECH_RECOGNITION_COMMAND_MAP);
                add(Constants.SPEECH_RECOGNITION_COMMAND_WEATHER);
            }
        };
        patternBasicCommand = String.format(Constants.PATTERN_FOR_SPEECH_BASIC_COMMAND, String.join(Constants.COMMAND_SEPARATOR, basicCommands));
    }

    //Method used to get SpeechRecognizer intent
    private Intent getSpeechRecognizerIntent() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        return intent;
    }


    //Method used to identify command based on user input
    private void identifyCommand(String command) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Pattern pattern = Pattern.compile(patternBasicCommand, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(command);
                if (matcher.matches()) {
                    Log.d(Constants.TAG_SPEECH_RECOGNIZER, "found basic command");
                    recognizeUserInput(matcher.group(Constants.BASIC_COMMAND_INDEX).trim().toLowerCase(), matcher.group(matcher.groupCount()).trim());
                } else {
                    Log.d(Constants.TAG_SPEECH_RECOGNIZER, "basic command not found");
                }
            }
        }).start();
    }

    //Method used to recognize module to be invoked based on user command
    private void recognizeUserInput(String basicCommand, String command) {
        switch (basicCommand) {

            case Constants.SPEECH_RECOGNITION_COMMAND_MAP:
                bundle = getInputForMap(command);
                sendMessageToHandler(Constants.SPEECH_RECOGNITION_COMMAND_MAP, bundle);
                break;

            case Constants.SPEECH_RECOGNITION_COMMAND_CALL:
                bundle = getInputForCall(command);
                sendMessageToHandler(Constants.SPEECH_RECOGNITION_COMMAND_CALL, bundle);
                break;

            case Constants.SPEECH_RECOGNITION_COMMAND_CALL_HI:

                sendMessageToHandler(Constants.SPEECH_RECOGNITION_COMMAND_CALL_HI, getCommandIdentifiedBundle());
                break;

            case Constants.SPEECH_RECOGNITION_COMMAND_CALL_BYE:
                sendMessageToHandler(Constants.SPEECH_RECOGNITION_COMMAND_CALL_BYE, getCommandIdentifiedBundle());
                break;

            case Constants.SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_PLAY:
                bundle = getInputForMusicPlayer(command);
                sendMessageToHandler(Constants.SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_PLAY, bundle);
                break;

            case Constants.SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_PAUSE:
                sendMessageToHandler(Constants.SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_PAUSE, getCommandIdentifiedBundle());
                break;

            case Constants.SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_PREVIOUS:
                sendMessageToHandler(Constants.SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_PREVIOUS
                        , getCommandIdentifiedBundle());
                break;

            case Constants.SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_STOP:
                sendMessageToHandler(Constants.SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_STOP, getCommandIdentifiedBundle());
                break;

            case Constants.SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_NEXT:
                sendMessageToHandler(Constants.SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_NEXT, getCommandIdentifiedBundle());
                break;

            case Constants.SPEECH_RECOGNITION_COMMAND_APPOINTMENTS:
                bundle = getInputForAppointments(command);
                sendMessageToHandler(Constants.SPEECH_RECOGNITION_COMMAND_APPOINTMENTS, bundle);
                break;

            case Constants.SPEECH_RECOGNITION_COMMAND_WEATHER:
                bundle = getInputForWeather(command);
                sendMessageToHandler(Constants.SPEECH_RECOGNITION_COMMAND_WEATHER, bundle);
                break;

            default:
                Log.d(Constants.TAG_SPEECH_RECOGNIZER, "identifyCommand - command not identified. User Command- " + command);
                bundle = new Bundle();
                bundle.putBoolean(Constants.SPEECH_RECOGNITION_COMMAND_IDENTIFIED, Constants.COMMAND_NOT_IDENTIFIED);
                sendMessageToHandler(null, bundle);
                break;
        }
    }


    //Method used to get input for weather module
    private Bundle getInputForWeather(String command) {
        Log.d(Constants.TAG_SPEECH_RECOGNIZER, "getInputForWeather - command: " + command);
        Pattern pattern = Pattern.compile(Constants.PATTERN_FOR_WEATHER);
        Matcher matcher = pattern.matcher(command);
        if (matcher.matches()) {
            bundle = getCommandIdentifiedBundle();
            Log.d(Constants.TAG_SPEECH_RECOGNIZER, "weather fetch condition: " + matcher.group(Constants.WEATHER_CONDITION_INDEX));
            bundle.putString(Constants.WEATHER_FETCH_CONDITION, matcher.group(Constants.WEATHER_CONDITION_INDEX));
        } else {
            Log.d(Constants.TAG_SPEECH_RECOGNIZER, "getInputForWeather - command: " + command);
        }
        return bundle;
    }


    //Method used to get appointment details
    private Bundle getInputForAppointments(String command) {
        Log.d(Constants.TAG_SPEECH_RECOGNIZER, "getInputForAppointments - command: " + command);
        bundle = getCommandIdentifiedBundle();

        String date = null;
        String time = null;

        //replace dot(.) if any in the string as speech recognizer will add dot for time meridiem (a.m./p.m.)
        command = command.replace(Constants.DOT, Constants.EMPTY_STRING);

        Pattern pattern = Pattern.compile(Constants.PATTERN_FOR_APPOINTMENT_DETAILS);
        Matcher matcher = pattern.matcher(command);
        if (matcher.matches()) {
            //user has provided details for setting appointment; otherwise need to show all appointments
            //command format - appointment .. remind me to <title> (on/today/tomorrow) <date> at <time> (a.m./p.m.) ..
            //e.g. command - appointment remind me to send email tomorrow at 3:30 p.m.

            //to get appointment title
            AddInputToBundleBasedonMatchGroup(bundle, matcher, Constants.APPOINTMENT_TITLE_INDEX, Constants.EXTRA_DATA_TITLE);

            //to get appointment date
            switch (matcher.group(Constants.APPOINTMENT_DATE_COMMAND_INDEX)) {
                case Constants.SPEECH_RECOGNITION_COMMAND_APPOINTMENTS_ON:
                    date = matcher.group(Constants.APPOINTMENT_DATE_INDEX).trim();

                    //remove date's ordinal indicator  e.g. 25th January
                    date = date.replaceAll(Constants.PATTERN_FOR_DAY, Constants.EMPTY_STRING);

                    if (date.split(Constants.SPACE).length == DATE_WITHOUT_YEAR_WORD_COUNT) {
                        //if user has not provided year. e.g. 7th July
                        date = ConversionHelper.convertDateStringToSpecificFormat(date, Constants.PATTERN_FOR_DATE_WITHOUT_YEAR, Constants.PATTERN_FOR_DATE);
                    } else {
                        //if user has provided year. e.g. 7th July 2021
                        date = ConversionHelper.convertDateStringToSpecificFormat(date, Constants.PATTERN_FOR_DATE, Constants.PATTERN_FOR_DATE);
                    }
                    //bundle.putString(Constants.APPOINTMENT_DATE, date);
                    break;

                case Constants.SPEECH_RECOGNITION_COMMAND_APPOINTMENTS_TODAY:
                    date = ConversionHelper.convertDateToString(LocalDate.now(), Constants.PATTERN_FOR_DATE);
                    //bundle.putString(Constants.APPOINTMENT_DATE, date);
                    break;
                case Constants.SPEECH_RECOGNITION_COMMAND_APPOINTMENTS_TOMORROW:
                    date = ConversionHelper.convertDateToString(LocalDate.now().plusDays(DAY_INCREMENT_COUNT), Constants.PATTERN_FOR_DATE);
                    //bundle.putString(Constants.APPOINTMENT_DATE, date);
                    break;
                default:
                    break;
            }
            Log.d(Constants.TAG_SPEECH_RECOGNIZER, "appointment date - " + date);

            //to get appointment time
            time = (matcher.group(Constants.APPOINTMENT_TIME_INDEX) + matcher.group(Constants.APPOINTMENT_TIME_AM_PM_INDEX)).trim();
            Log.d(Constants.TAG_SPEECH_RECOGNIZER, "appointment time - " + time);
            Date appointmentDate = ConversionHelper.convertStringToDateTime(date + " " + time, Constants.PATTERN_FOR_DATE_TIME);
            bundle.putSerializable(Constants.EXTRA_DATE_AND_TIME, appointmentDate);
            Log.d(Constants.TAG_SPEECH_RECOGNIZER, "appointment date-time - " + appointmentDate.toString());
            //bundle.putString(Constants.APPOINTMENT_TIME, time);
        }
        return bundle;
    }

    //Method used to get song for playing music
    private Bundle getInputForMusicPlayer(String command) {
        Log.d(Constants.TAG_SPEECH_RECOGNIZER, "getInputForMap - user input " + command);
        bundle = getCommandIdentifiedBundle();
        bundle.putString(Constants.SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_PLAY, command);
        return bundle;
    }

    //Method used to get inputs for map command
    private Bundle getInputForMap(String command) {
        Log.d(Constants.TAG_SPEECH_RECOGNIZER, "getInputForMap - user input: " + command);

        boolean commandIdentified = false;
        bundle = new Bundle();
        String location;
        ArrayList<String> mapExpressions = new ArrayList<String>() {
            {
                add(Constants.PATTERN_FOR_MAP_DIRECTION);
                add((Constants.PATTERN_FOR_MAP_TO_LOCATION));
                add(Constants.PATTERN_FOR_MAP_USB);
            }
        };

        //Identify commands - map ..from..to.., map to.., map usb
        for (String expression : mapExpressions) {
            Pattern pattern = Pattern.compile(expression);
            Matcher matcher = pattern.matcher(command);
            if (matcher.matches()) {
                switch (expression) {
                    case Constants.PATTERN_FOR_MAP_DIRECTION:
                        //to get from and to locations for direction. e.g.- Command:map get direction from Bangalore City Railway Station to ootty
                        //From location
                        AddInputToBundleBasedonMatchGroup(bundle, matcher, Constants.MAP_DIRECTION_FROM_LOCATION_INDEX, Constants.EXTRA_DATA_FROM);

                        //To location
                        AddInputToBundleBasedonMatchGroup(bundle, matcher, Constants.MAP_DIRECTION_TO_LOCATION_INDEX, Constants.EXTRA_DATA_TO);
                        commandIdentified = true;
                        break;

                    case Constants.PATTERN_FOR_MAP_TO_LOCATION:
                        //to get to location. e.g.- Command:map go to ootty
                        AddInputToBundleBasedonMatchGroup(bundle, matcher, Constants.MAP_TO_LOCATION_INDEX, Constants.EXTRA_DATA_TO);
                        commandIdentified = true;
                        break;

                    case Constants.PATTERN_FOR_MAP_USB:
                        //to check whether it is usb command e.g.-Command:map usb
                        bundle.putString(Constants.EXTRA_DATA_OPEN_USB, Constants.EXTRA_DATA_OPEN_USB);
                        Log.d(Constants.TAG_SPEECH_RECOGNIZER, "detected input - usb");
                        commandIdentified = true;
                    default:
                        break;
                }
                break;
            }
        }
        if (commandIdentified) {
            bundle.putBoolean(Constants.SPEECH_RECOGNITION_COMMAND_IDENTIFIED, Constants.COMMAND_IDENTIFIED);
        } else {
            Log.d(Constants.TAG_SPEECH_RECOGNIZER, "getInputForMap - COMMAND_NOT_IDENTIFIED");
            bundle.putBoolean(Constants.SPEECH_RECOGNITION_COMMAND_IDENTIFIED, Constants.COMMAND_NOT_IDENTIFIED);
        }
        return bundle;
    }


    //Method used to get user input corresponding to call command
    private Bundle getInputForCall(String command) {
        Log.d(Constants.TAG_SPEECH_RECOGNIZER, "getInputForCall - user input " + command);
        bundle = getCommandIdentifiedBundle();

        //check whether user input contains only letters i.e; whether user has provided contact name
        Pattern pattern = Pattern.compile(Constants.PATTERN_FOR_LETTERS_AND_SPACE);
        Matcher matcher = pattern.matcher(command);
        if (matcher.matches()) {
            //user has provided contact name
            Log.d(Constants.TAG_SPEECH_RECOGNIZER, "getInputForCall - Detected user has provided contact name: " + command);
            bundle.putString(Constants.CONTACT_NAME, command);

        } else {
            Log.d(Constants.TAG_SPEECH_RECOGNIZER, "getInputForCall - Detected user has provided phone number");
            //user has provided phone number
            //Extract phone number from input
            StringBuilder phoneNumber = new StringBuilder();
            pattern = Pattern.compile(Constants.PATTERN_FOR_DIGITS);
            matcher = pattern.matcher(command);
            while (matcher.find()) {
                phoneNumber.append(matcher.group());
            }
            if (phoneNumber.length() > 0) {
                bundle.putString(Constants.CONTACT_NUMBER, phoneNumber.toString());
                Log.d(Constants.TAG_SPEECH_RECOGNIZER, "getInputForCall -phone number: " + phoneNumber);
            }
        }
        return bundle;
    }


    private Bundle getCommandIdentifiedBundle() {
        bundle = new Bundle();
        bundle.putBoolean(Constants.SPEECH_RECOGNITION_COMMAND_IDENTIFIED, Constants.COMMAND_IDENTIFIED);
        return bundle;
    }


    //Method used add item to bundle
    private void AddInputToBundleBasedonMatchGroup(Bundle bundle, Matcher matcher, int index, String bundleKey) {
        String input = matcher.group(index).trim();
        bundle.putString(bundleKey, input);
        Log.d(Constants.TAG_SPEECH_RECOGNIZER, "Identified user input - " + bundleKey + " : " + input);
    }


    //Method used to commands and inputs to handler
    private void sendMessageToHandler(String command, Bundle bundle) {
        if (handler != null) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            Message message = handler.obtainMessage();
            message.what = Constants.SPEECH_RECOGNITION_CODE;
            if (command != null) {
                bundle.putString(Constants.COMMAND, command);
            }
            if (bundle.size() > 0) {
                message.setData(bundle);
            }
            handler.sendMessage(message);
        }
    }


    //Method used to get handler
    private Handler getHandler() {
        return new Handler() {
            @Override
            public void handleMessage(Message message) {
                if (message.what == Constants.SPEECH_RECOGNITION_CODE) {
                    Bundle data = message.getData();
                    if (data != null) {
                        //send user input to other modules by using local broadcast

                        Intent intent = new Intent(SpeechRecognitionService.this, SpeechRecognitionReceiver.class);
                        //Intent intent = new Intent();
                        intent.setAction(Constants.BROADCAST_ACTION_SPEECH_RECOGNITION);
                        intent.putExtras(data);
                        //LocalBroadcastManager.getInstance(SpeechRecognitionService.this).sendBroadcast(intent);
                        sendBroadcast(intent);
                        Log.d(Constants.TAG_SPEECH_RECOGNIZER, "sending broadcast");
                    }
                }
            }
        };
    }

}