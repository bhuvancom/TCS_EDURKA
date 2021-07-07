package com.tcs.edureka.providers;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceProvider;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.ListBuilder.RowBuilder;
import androidx.slice.builders.SliceAction;

import com.google.android.gms.maps.model.LatLng;
import com.tcs.edureka.R;
import com.tcs.edureka.ui.activity.MainActivity;
import com.tcs.edureka.ui.activity.MyPreferencesActivity;
import com.tcs.edureka.ui.activity.map.MapActivity;
import com.tcs.edureka.utility.Constants;
import com.tcs.edureka.utility.Utility;

import org.jetbrains.annotations.NotNull;

public class MySliceProvider extends SliceProvider {

    private static final String TAG = "MySliceProvider";

    /**
     * Instantiate any required objects. Return true if the provider was successfully created,
     * false otherwise.
     */
    @Override
    public boolean onCreateSliceProvider() {
        return true;
    }

    /**
     * Converts URL to content URI (i.e. content://com.bhuvaneshvar.a1acceptcallbysayinghi...)
     */
    @Override
    @NonNull
    public Uri onMapIntentToUri(@Nullable Intent intent) {
        // Note: implementing this is only required if you plan on catching URL requests.
        // This is an example solution.
        Uri.Builder uriBuilder = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT);
        if (intent == null) return uriBuilder.build();
        Uri data = intent.getData();
        if (data != null && data.getPath() != null) {
            String path = data.getPath().replace("/", "");
            uriBuilder = uriBuilder.path(path);
        }
        Context context = getContext();
        if (context != null) {
            uriBuilder = uriBuilder.authority(context.getPackageName());
        }
        return uriBuilder.build();
    }

    /**
     * Construct the Slice and bind data if available.
     */
    public Slice onBindSlice(@NotNull Uri sliceUri) {

        Context context = getContext();
        SliceAction activityAction = createActivityAction();
        if (context == null || activityAction == null) {
            return null;
        }
        if ("/map".equals(sliceUri.getPath())) {

            return new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                    .addRow(
                            new RowBuilder()
                                    .setTitle(getTitle())
                                    .setSubtitle(getSubtitle())
                                    .setPrimaryAction(openMapActivity())
                    )
                    .build();
        } else {

            return new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                    .addRow(
                            new RowBuilder()
                                    .setTitle("URI not found.")
                                    .setPrimaryAction(activityAction)
                    )
                    .build();
        }
    }

    private String getTitle() {
        if (Utility.getPreffLocation() != null) {
            return Utility.sliceTitle;
        }
        return "Preferred location not set, click to set";
    }

    private String getSubtitle() {
        if (Utility.sliceSubtitle.trim().isEmpty()) {
            return "";
        }
        return Utility.sliceSubtitle.trim();
    }

    private SliceAction openMapActivity() {

        LatLng string = Utility.getUserPrefLocation();

        Intent intent;
        if (string != null) {
            if (Utility.sliceTitle.contains("calculating")) intent = new Intent();
            else {
                intent = new Intent(getContext(), MapActivity.class);
                intent.putExtra(Constants.OPEN_MAP_WITH_PREFERRED_LOCATION, "OPEN_MAP_WITHOUT_DESTINATION");
            }
        } else {
            Utility.makeToast("Please set your proffered Location", getContext());
            Log.d(TAG, "openMapActivity: opening preff activity to et");
            intent = new Intent(getContext(), MyPreferencesActivity.class);
        }

        return SliceAction.create(PendingIntent.getActivity(
                getContext(),
                0,
                intent,
                0
                ),
                IconCompat.createWithResource(getContext(), R.drawable.ic_launcher_foreground),
                ListBuilder.ICON_IMAGE,
                "Open Map"
        );


    }

    private SliceAction createActivityAction() {
        //Instead of returning null, you should create a SliceAction. Here is an example:
        return SliceAction.create(
                PendingIntent.getActivity(
                        getContext(), 0, new Intent(getContext(), MainActivity.class), 0
                ),
                IconCompat.createWithResource(getContext(), R.drawable.ic_launcher_foreground),
                ListBuilder.ICON_IMAGE,
                "Open App"
        );

    }

    /**
     * Slice has been pinned to external process. Subscribe to data source if necessary.
     */
    @Override
    public void onSlicePinned(Uri sliceUri) {
        // When data is received, call context.contentResolver.notifyChange(sliceUri, null) to
        // trigger MySliceProvider#onBindSlice(Uri) again.
    }

    /**
     * Unsubscribe from data source if necessary.
     */
    @Override
    public void onSliceUnpinned(Uri sliceUri) {
        // Remove any observers if necessary to avoid memory leaks.
    }
}