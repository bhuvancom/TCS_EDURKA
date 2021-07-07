package com.tcs.edureka.ui.activity.call

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tcs.edureka.databinding.ActivityCallBinding
import com.tcs.edureka.model.CallLogModel
import com.tcs.edureka.receivers.CallBroadcastReceiver
import com.tcs.edureka.receivers.CallReceiver
import com.tcs.edureka.utility.Constants
import com.tcs.edureka.utility.Utility
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


/**
 * @author Bhuvaneshvar
 */

private const val TAG = "CallActivity"

@AndroidEntryPoint
class CallActivity : AppCompatActivity(), CallReceiver {

    private var isIncoming = false
    private var isCallOnGoing = false

    private val myCallBroadcastReceiver by lazy {
        CallBroadcastReceiver()
    }

    private val callViewModel by lazy {
        ViewModelProvider(this).get(CallViewModel::class.java)
    }

    private val callAdapter by lazy {
        CallAdapter {
            makeCall(it.toString())
        }
    }

    fun makeCall(mob: String) {
        Log.d(TAG, "makeCall: $mob")
        try {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$mob")
            startActivity(intent)
        } catch (e: java.lang.Exception) {
            Toast.makeText(this,
                    "Unable to call at this time", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var binding: ActivityCallBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val shouldSearchContactByName = intent.getStringExtra(Constants.CONTACT_NAME)
        val shouldSearchByNumber = intent.getStringExtra(Constants.CONTACT_NUMBER)

        if (shouldSearchByNumber != null) {
            makeCall(shouldSearchByNumber)
        }
        if (shouldSearchContactByName != null) {
            getRawContactId(shouldSearchContactByName)
        }



        Utility.makeToast("Swipe to delete", this)
        binding.recyclerViewCalls.apply {
            layoutManager = LinearLayoutManager(this@CallActivity, LinearLayoutManager.VERTICAL, false)
            adapter = callAdapter
        }
        callViewModel.getAllCallLog().observe(this) {
            callAdapter.differ.submitList(it)
            Log.d(TAG, "onCreate: $it")
            it?.let {
                if (it.isEmpty()) {
                    binding.tvNoCallLog.isVisible = true
                    binding.recyclerViewCalls.isVisible = false
                } else {
                    binding.tvNoCallLog.isVisible = false
                    binding.recyclerViewCalls.isVisible = true
                }
            }
        }


        val simpleCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                val model: CallLogModel = callAdapter.differ.currentList[pos]
                callViewModel.deleteCall(model)
            }
        }

        val helper = ItemTouchHelper(simpleCallback)
        helper.attachToRecyclerView(binding.recyclerViewCalls)
    }

    private fun getRawContactId(name: String) {
        try {
            var isSuccess = false
            val readContactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            val cursor = contentResolver.query(readContactUri,
                    null,
                    "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?",
                    listOf("$name%").toTypedArray(),
                    null)
            cursor?.moveToFirst()
            val phoneNumberIndex: Int? = cursor?.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            phoneNumberIndex?.let {
                val phoneNumber: String? = cursor.getString(phoneNumberIndex)
                phoneNumber?.let {
                    isSuccess = true
                    makeCall(it)
                }
            }
            cursor?.close()
            if (!isSuccess) {
                Utility.makeToast("Unable to search...", this)
            }
        } catch (e: Exception) {
            Log.d(TAG, "getRawContactId: $e")
        }
    }

    override fun onStart() {
        super.onStart()
        myCallBroadcastReceiver.setCallReceiver(this)
        val intent = IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        registerReceiver(myCallBroadcastReceiver, intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(myCallBroadcastReceiver)
    }

    override fun onIncomingCallStarted(ctx: Context?, number: String?, start: Date?) {
        isIncoming = true
        isCallOnGoing = false
    }

    override fun onIncomingCallEnded(ctx: Context?, number: String?, start: Date?, end: Date?) {
        isIncoming = false
        isCallOnGoing = false
    }

    override fun onMissedCall(ctx: Context?, number: String?, start: Date?) {
        isIncoming = false
    }

    override fun onPhonePicked(ctx: Context?, number: String?, start: Date?) {
        isIncoming = false
        isCallOnGoing = true
    }
}