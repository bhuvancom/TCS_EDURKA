package com.tcs.edureka.receivers;

import android.content.Context;

import java.util.Date;

/**
 * @author Bhuvaneshvar
 */
public interface CallReceiver {
    //Derived classes should override these to respond to specific events of interest
    void onIncomingCallStarted(Context ctx, String number, Date start);

    void onIncomingCallEnded(Context ctx, String number, Date start, Date end);

    void onMissedCall(Context ctx, String number, Date start);

    void onPhonePicked(Context ctx, String number, Date start);
}
