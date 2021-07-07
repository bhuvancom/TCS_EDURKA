package com.tcs.edureka.utility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * @author Bhuvaneshvar
 */
public class Utility {
    private static final String TAG = "Utility";
    public static String sliceTitle = "";
    public static String sliceSubtitle = "";
    public static String[] MONTH_LIST = new String[]{"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};
    public static String PREFF_CITY = "";
    public static String CURRENT_USER_NAME = "";
    private static LatLng currentUserPrefLocation = null;
    private static LatLng PREFF_LAT_LAN = null;

    public static boolean isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static String getPreffCity() {
        return PREFF_CITY;
    }

    public static String getTodayTommorowOrDate(Date date) {
        int hours = date.getHours();
        int minute = date.getMinutes();
        if (DateUtils.isToday(date.getTime()))
            return "Today at " + hours + " : " + minute;

        if (DateUtils.isToday(date.getTime() - DateUtils.DAY_IN_MILLIS))
            return "Tomorrow at " + hours + " : " + minute;

        return new StringBuilder().append(date.getDate()).append("/") //-> 7/
                .append(MONTH_LIST[date.getMonth() + 1]).append("/") //-> August/
                .append(date.getYear() + 1900)
                .append(" at ").append(hours).append(" : ").append(minute)
                .toString();

    }

    public static void setPreffLatLan(Context context) {
        LatLng latLng = null;
        SharedPreferences defaultSharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String aFloat = defaultSharedPreferences.getString(Constants.LATITUDE, "");
        String bFlot = defaultSharedPreferences.getString(Constants.LONGITUDE, "");

        if (!(bFlot.trim().isEmpty() && aFloat.trim().isEmpty())) {
            try {
                double lat = Double.parseDouble(aFloat);
                double log = Double.parseDouble(bFlot);
                if (lat > 0 && log > 0) {
                    Log.d(TAG, "setPreffLatLan: adding " + lat + " " + log);
                    latLng = new LatLng(lat, log);
                }
            } catch (NumberFormatException ignored) {
                Log.d(TAG, "setPreffLatLan: " + ignored.getMessage() + " " + aFloat + " " + bFlot);
            }

        }

        PREFF_LAT_LAN = latLng;
    }

    public static LatLng getUserPrefLocation() {
        return PREFF_LAT_LAN;
    }

    public static String getFromSharedPref(String key, Context context) {
        SharedPreferences defaultSharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getString(key, "");
    }

    public static boolean getCheckedFromPref(String key, Context context) {
        SharedPreferences defaultSharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences.getBoolean(key, false);
    }

    public static String getCurrentUserName() {
        return CURRENT_USER_NAME;
    }

    public static LatLng getPreffLocation() {
        return getUserPrefLocation();
    }

    public static void setPreffLocation(LatLng latLng) {
        currentUserPrefLocation = latLng;
    }

    public static void updateSliceTextAndSubtitle(@NotNull String locationName, String title, String subtitle, Context context) {
        sliceTitle = title;
        sliceSubtitle = locationName + " " + subtitle;
        Uri uri = getUri(context, "map");
        context.getContentResolver().notifyChange(uri, null);

    }

    public static void makeToast(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    public static Uri getUri(Context context, String path) {
        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(context.getPackageName() + ".providers")
                .appendPath(path)
                .build();
    }
}
