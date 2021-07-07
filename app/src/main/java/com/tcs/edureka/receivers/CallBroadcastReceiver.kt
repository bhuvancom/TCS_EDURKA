package com.tcs.edureka.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.tcs.edureka.db.AppDataBase
import com.tcs.edureka.db.AppDataBase.Companion.getDatabase
import com.tcs.edureka.model.CallLogModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CallBroadcastReceiver : BroadcastReceiver() {
    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var callStartTime: Date? = null
    private var isIncoming = false
    private var savedNumber: String? = null

    private var callReceiver: CallReceiver? = null
    fun getCallReceiver(): CallReceiver? {
        return callReceiver
    }

    fun setCallReceiver(callReceiver: CallReceiver?) {
        this.callReceiver = callReceiver
    }

    private var appDataBase: AppDataBase? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return

        val action = intent?.action
        if (action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            appDataBase = getDatabase(context)
            val stateStr = intent.extras!!.getString(TelephonyManager.EXTRA_STATE)
            val number = intent.extras!!.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
            var state = 0
            if (stateStr == TelephonyManager.EXTRA_STATE_IDLE) {
                state = TelephonyManager.CALL_STATE_IDLE
            } else if (stateStr == TelephonyManager.EXTRA_STATE_OFFHOOK) {
                state = TelephonyManager.CALL_STATE_OFFHOOK
            } else if (stateStr == TelephonyManager.EXTRA_STATE_RINGING) {
                state = TelephonyManager.CALL_STATE_RINGING
            }
            onCallStateChanged(context, state, number)
        }
    }

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    private fun onCallStateChanged(context: Context?, state: Int, number: String?) {
        if (lastState == state) {
            //No change, debounce extras
            return
        }
        val appointmentDao = appDataBase?.getCallLogDao()
        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                isIncoming = true
                callStartTime = Date()
                savedNumber = number
                if (callReceiver != null) callReceiver!!.onIncomingCallStarted(context, number, callStartTime)
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                Log.d(TAG, "onCallStateChanged: $savedNumber")
                val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm aa")
                savedNumber?.let {
                    val callLogModel = CallLogModel(
                            null,
                            savedNumber!!,
                            savedNumber!!.toLong(),
                            "RECEIVED",
                            sdf.format(callStartTime).toString()
                    )
                    Log.d(TAG, "onCallStateChanged: received will $it $callLogModel")
                    GlobalScope.launch(Dispatchers.IO) {
                        Log.d(TAG, "onCallStateChanged: will save $callLogModel")
                        appointmentDao?.upsert(callLogModel)
                    }
                }
                if (callReceiver != null) {
                    callReceiver!!.onPhonePicked(context, number, callStartTime)
                }
            }
            TelephonyManager.CALL_STATE_IDLE -> {            //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    Log.d(TAG, "onCallStateChanged: missed")

                    savedNumber?.let {
                        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm aa")
                        Log.d(TAG, "onCallStateChanged: nmbr $it")
                        val callLogModel = CallLogModel(
                                null,
                                savedNumber!!,
                                savedNumber!!.toLong(),
                                "MISSED",
                                sdf.format(callStartTime).toString()
                        )
                        GlobalScope.launch(Dispatchers.IO) {
                            Log.d(TAG, "onCallStateChanged: will save $callLogModel")
                            appointmentDao?.upsert(callLogModel)
                        }
                        if (callReceiver != null) {
                            callReceiver!!.onMissedCall(context, savedNumber, callStartTime)
                        }
                    }
                } else if (isIncoming) {
                    if (callReceiver != null) callReceiver!!.onIncomingCallEnded(context, savedNumber, callStartTime, Date())
                }
            }
        }
        lastState = state
    }
}

private const val TAG = "CallBroadcastReceiver"