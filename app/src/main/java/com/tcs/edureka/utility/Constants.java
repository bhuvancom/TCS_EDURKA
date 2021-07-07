package com.tcs.edureka.utility;

/**
 * @author Bhuvaneshvar
 */
public class Constants {
    //extras for map
    public static final String EXTRA_DATA_FROM = "EXTRA_DATA_FROM";
    public static final String EXTRA_DATA_TO = "EXTRA_DATA_TO";
    public static final String EXTRA_DATA_OPEN_USB = "USB";
    public static final String OPEN_MAP_WITH_PREFERRED_LOCATION = "OPEN_MAP_WITHOUT_DESTINATION";
    //extra for appointments
    public static final String EXTRA_DATA_TITLE = "TITLE";
    public static final String EXTRA_DATE_AND_TIME = "DATE_AND_TIME";
    public static final String APPOINTMENT_ACTION = "APPOINTMENT_ALARM";


    //Notification related
    public static final String CHANNEL_ID = "TCS_with_sound";
    public static final String CHANNEL_ID_WITHOUT_SOUND = "TCS_without_sound";
    public static final String CHANNEL_ID_STICKY = "TCS_stiky";

    //constant for determining activity
    public static final String MAP = "MAP";
    public static final String CONTACTS = "CONTACTS";
    public static final String CALL = "CALL";
    public static final String CALENDAR = "CALENDAR";
    public static final String MEDIA = "MEDIA";
    public static final String WEATHER = "WEATHER";


    //pref
    public static final String USERNAME = "userName";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String PREFERRED_LOCATION = "prefLocation";
    public static final String PREFERRED_CITY = "prefCity";
    public static final String SLICE = "slice";
    public static final String SPEECH = "speech";

    //music player
    public static final String MUSIC_ACTION = "MUSIC_ACTION";
    public static final String MUSIC_ACTION_TO_ACTIVITY = "MUSIC_ACTION_TO_ACTIVITY";
    public static final String MUSIC_ACTION_TO_ACTIVITY_PROGRESS = "MUSIC_ACTION_TO_ACTIVITY_PROGRESS";
    public static final String MUSIC_ACTION_TO_ACTIVITY_DURATION = "MUSIC_ACTION_TO_ACTIVITY_DURATION";
    public static final String MUSIC_ACTION_TO_ACTIVITY_ERROR = "MUSIC_ACTION_TO_ACTIVITY_ERROR";
    public static final String MUSIC_ACTION_TO_ACTIVITY_BUFFERRED = "MUSIC_ACTION_TO_ACTIVITY_BUFFERRED";
    public static final String MUSIC_ACTION_PLAY = "PLAY";
    public static final String MUSIC_ACTION_PAUSE = "PAUSE";
    public static final String MUSIC_ACTION_STOP = "STOP";
    public static final String MUSIC_ACTION_SHEEK = "SHEEK";
    public static final String MUSIC_EXTRA_SONG = "SONG";
    public static final String BINDER_STATE = "BINDER_STATE";
    public static final String MUSIC_ACTION_RESUMING = "RESUMING";
    public static final String MUSIC_EXTRA_LAST_INDEX = "MUSIC_EXTRA_LAST_INDEX";
    public static final String MUSIC_ACTION_NEXT = "NEXT";
    public static final String MUSIC_ACTION_PREVIOUS = "PREVIOUS";
    public static final String MUSIC_ACTION_COMPLETED = "COMPLETED";


    /*
     * @author Bhavya Bhanu
     */
    public static final String BROADCAST_ACTION_SPEECH_RECOGNITION = "com.tcs.edureka.speechrecognition";
    //Keys for identifying user inputs - used in speech recognition
    public static final String COMMAND = "Command";
    public static final String CONTACT_NAME = "name";
    public static final String CONTACT_NUMBER = "number";
    public static final String APPOINTMENT_TITLE = "title";
    public static final String APPOINTMENT_DATE = "date";
    public static final String APPOINTMENT_TIME = "time";
    public static final String WEATHER_FETCH_CONDITION = "condition";
    public final static String SPEECH_RECOGNITION_COMMAND_IDENTIFIED = "Command identified";
    public final static boolean COMMAND_IDENTIFIED = true;
    public final static boolean COMMAND_NOT_IDENTIFIED = false;

    public static final String EMPTY_STRING = "";
    public static final String SPACE = " ";
    public static final String DOT = ".";
    public static final String COMMAND_SEPARATOR = "\\b)|(\\b";

    //Index for recognising input - used in speech recognition
    public static final int MAP_DIRECTION_FROM_LOCATION_INDEX = 3;
    public static final int MAP_DIRECTION_TO_LOCATION_INDEX = 5;
    public static final int MAP_TO_LOCATION_INDEX = 3;
    public static final int APPOINTMENT_TITLE_INDEX = 3;
    public static final int APPOINTMENT_DATE_COMMAND_INDEX = 4;
    public static final int APPOINTMENT_DATE_INDEX = 8;
    public static final int APPOINTMENT_TIME_INDEX = 10;
    public static final int APPOINTMENT_TIME_AM_PM_INDEX = 11;
    public static final int WEATHER_CONDITION_INDEX = 2;
    public static final int BASIC_COMMAND_INDEX = 2;
    //-------Start of SPEECH_RECOGNITION_COMMAND ------
    //For Maps
    public static final String SPEECH_RECOGNITION_COMMAND_MAP = "map";
    //public static final String SPEECH_RECOGNITION_COMMAND_MAP_FROM = "from";
    //public static final String SPEECH_RECOGNITION_COMMAND_MAP_TO = "to";
    //public static final String SPEECH_RECOGNITION_COMMAND_MAP_USB = "USB";

    //For Call
    public static final String SPEECH_RECOGNITION_COMMAND_CALL = "call";
    public static final String SPEECH_RECOGNITION_COMMAND_CALL_HI = "hi";
    public static final String SPEECH_RECOGNITION_COMMAND_CALL_BYE = "bye";

    //For Music player
    public static final String SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_PLAY = "play";
    public static final String SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_PAUSE = "pause music";
    public static final String SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_STOP = "stop music";
    public static final String SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_PREVIOUS = "previous music";
    public static final String SPEECH_RECOGNITION_COMMAND_MUSIC_PLAYER_NEXT = "next music";

    //For appointments
    public static final String SPEECH_RECOGNITION_COMMAND_APPOINTMENTS = "appointment";
    public static final String SPEECH_RECOGNITION_COMMAND_APPOINTMENTS_ON = "on";
    public static final String SPEECH_RECOGNITION_COMMAND_APPOINTMENTS_TODAY = "today";
    public static final String SPEECH_RECOGNITION_COMMAND_APPOINTMENTS_TOMORROW = "tomorrow";

    //For weather
    public static final String SPEECH_RECOGNITION_COMMAND_WEATHER = "weather";
    public static final String SPEECH_RECOGNITION_COMMAND_WEATHER_TODAY = "today";
    public static final String SPEECH_RECOGNITION_COMMAND_WEATHER_WEEK = "week";
    public static final String SPEECH_RECOGNITION_COMMAND_WEATHER_MONTH = "month";

    //-------End of SPEECH_RECOGNITION_COMMAND ------


    public static final String TAG_SPEECH_RECOGNIZER = "speech recognizer";
    public static final int SPEECH_RECOGNITION_CODE = 100;

    //Patterns for speech recognition
    public static final String PATTERN_FOR_LETTERS_AND_SPACE = "^[ A-Za-z]+$";
    public static final String PATTERN_FOR_DIGITS = "\\d+";
    public static final String PATTERN_FOR_SPEECH_BASIC_COMMAND = "(.*)((\\b%s\\b))(.*)";
    public static final String PATTERN_FOR_MAP_DIRECTION = "(.*)(\\bfrom\\b)(.*)(\\bto\\b)(.*)";
    public static final String PATTERN_FOR_MAP_TO_LOCATION = "(.*)(\\bto\\b)(.*)";
    public static final String PATTERN_FOR_MAP_USB = "(.*)(\\bUSB\\b)(.*)";
    public static final String PATTERN_FOR_APPOINTMENT_DETAILS = "(.*)(remind me to)(.*)((\\bon\\b)|(\\btoday\\b)|(\\btomorrow\\b))(.*)(\\bat\\b)(.*)((\\bam\\b)|(\\bpm\\b))(.*)";
    public static final String PATTERN_FOR_DAY = "((?<=\\d)(st|nd|rd|th))";
    public static final String PATTERN_FOR_DATE = "d MMMM yyyy";
    public static final String PATTERN_FOR_DATE_WITHOUT_YEAR = "d MMMM";
    public static final String PATTERN_FOR_DATE_TIME = "d MMMM yyyy hh:mm a";
    public static final String PATTERN_FOR_WEATHER = "(.*)((today)|(week)|(month))(.*)";

}
