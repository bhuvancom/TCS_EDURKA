package com.tcs.edureka.utility;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/*
 * @author Bhavya Bhanu
 */
public class ConversionHelper {

    //Convert date string in particular format to the required string format
    public static String convertDateStringToSpecificFormat(String date, String currentFormat, String requiredFormat) {
        String convertedDate = null;
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern(currentFormat)
                .parseDefaulting(ChronoField.YEAR, Calendar.getInstance().get(Calendar.YEAR))
                .toFormatter(Locale.getDefault());
        try {
            convertedDate = convertDateToString(LocalDate.parse(date, formatter), requiredFormat);
        } catch (DateTimeParseException exception) {
            exception.printStackTrace();
            Log.d("tag", "ConversionHelper - date conversion failed for input : " + date + "\nMessage -\n" + exception.getMessage());
        }
        return convertedDate;
    }

    //Convert date to string in the specified format
    public static String convertDateToString(LocalDate date, String format) {
        return date.format(DateTimeFormatter.ofPattern(format));
    }

    //Convert string to date
    public static Date convertStringToDateTime(String date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date convertedDate = null;
        try {
            convertedDate = formatter.parse(date);
        } catch (ParseException exception) {
            exception.printStackTrace();
            Log.d("tag", "ConversionHelper - convertStringToDateTime - date conversion failed for input : " + date + "\nMessage -\n" + exception.getMessage());

        }
        return convertedDate;
    }
}
