package com.tcs.edureka.utility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

/**
 * @author Bhuvaneshvar
 */
public class Utility {
    private static final String TAG = "Utility";
    public static String sliceTitle = "";
    public static String sliceSubtitle = "";
    private static LatLng currentUserPrefLocation = null;

    public static boolean isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static String getCurrentUserName() {
        return "bhuavan1";
    }

    public static LatLng getPreffLocation() {
        return currentUserPrefLocation;
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

    // utility to get location name by given lat long
    public static String getLocationName(Double lat, Double longi, Context context) {
        Geocoder geo = new Geocoder(context);
        StringBuilder builder = new StringBuilder();
        List<Address> fromLocation;
        try {
            fromLocation = geo.getFromLocation(lat, longi, 1);
            if (fromLocation.size() > 0) {
                Address address = fromLocation.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    builder.append(address.getAddressLine(i)).append("\n");
                }
            }
        } catch (IOException e) {
            Log.d(TAG, "getLocationName: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
        return builder.toString();
    }

    public static Uri getUri(Context context, String path) {
        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(context.getPackageName() + ".providers")
                .appendPath(path)
                .build();
    }
}
