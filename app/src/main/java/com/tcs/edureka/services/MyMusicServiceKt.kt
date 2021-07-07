package com.tcs.edureka.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.media.MediaPlayer
import android.media.MediaPlayer.*
import android.os.Binder
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import com.tcs.edureka.model.mediaplayer.MediaModel
import com.tcs.edureka.utility.Constants
import com.tcs.edureka.utility.Utility
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * @author Bhuvaneshvar
 */
class MyMusicServiceKt : Service(), OnCompletionListener,
        OnPreparedListener, OnErrorListener, OnSeekCompleteListener,
        OnInfoListener, OnBufferingUpdateListener, OnAudioFocusChangeListener {

    inner class LocalBind : Binder() {
        fun getService() = MyMusicServiceKt()
    }

    private val TAG = "MyMusicPlayerService"
    private var audioManager: AudioManager? = null
    private var mediaPlayer: MediaPlayer? = null

    private var currentSong: MediaModel? = null

    //Used to pause/resume MediaPlayer
    private var resumePosition = 0

    //Handle incoming phone calls
    private var ongoingCall = false
    private var phoneStateListener: PhoneStateListener? = null
    private var telephonyManager: TelephonyManager? = null

    private val binder = LocalBind()
    private var seekBarRunner: Runnable? = null

    private var lastSongIndex = -1
    private var mExecutor: ScheduledExecutorService? = null


    override fun onBind(intent: Intent?): IBinder {
        return binder
    }


    override fun onCreate() {
        Log.d(TAG, "onCreate: called")
        super.onCreate()
        callStateListener()
        registerBecomingNoisyReceiver()
        registerPlayNewaudio()
    }

    private fun initMediaPlayer() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer!!.stop()
                mediaPlayer!!.reset()
                currentSong?.let { mediaPlayer!!.setDataSource(it.src) }
                mediaPlayer!!.prepareAsync()
                return
            }

            mediaPlayer = MediaPlayer()
            //Set up MediaPlayer event listeners
            mediaPlayer!!.setOnCompletionListener(this)
            mediaPlayer!!.setOnErrorListener(this)
            mediaPlayer!!.setOnPreparedListener(this)
            mediaPlayer!!.setOnBufferingUpdateListener(this)
            mediaPlayer!!.setOnSeekCompleteListener(this)
            mediaPlayer!!.setOnInfoListener(this)
            //Reset so that the MediaPlayer is not pointing to another data source
            mediaPlayer!!.reset()

            mediaPlayer!!.setAudioAttributes(AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build())

            // Set the data source to the mediaFile location
            Log.d(TAG, "initMediaPlayer: ${currentSong?.src}")
            currentSong?.let { mediaPlayer!!.setDataSource(it.src) }
        } catch (e: IOException) {
            sendMediaState(Constants.MUSIC_ACTION_TO_ACTIVITY_ERROR)
            e.printStackTrace()
            Log.d(TAG, "initMediaPlayer: error")
            stopSelf()
        }
        mediaPlayer!!.prepareAsync()
    }

    private fun playMedia() {
        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
            startSendingProgress(true)
            sendMediaState(Constants.MUSIC_ACTION_PLAY)
        }
    }

    private fun startSeekBarRunner() {
        if (seekBarRunner == null) {
            seekBarRunner = Runnable {
                updateProgress()
            }
        }
    }

    private fun stopMedia() {
        if (mediaPlayer == null) return
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
            resumePosition = 0
            sendMediaState(Constants.MUSIC_ACTION_STOP)

        }

        startSendingProgress(false)
    }

    private fun updateProgress() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                sendProgressBroadcast(it.currentPosition)

            }
        }
    }

    private fun startSendingProgress(shouldSend: Boolean) {
        if (!shouldSend) {

            seekBarRunner = null
            mExecutor?.shutdownNow()
            mExecutor = null
        } else {
            if (mExecutor == null) mExecutor = Executors.newSingleThreadScheduledExecutor()
            startSeekBarRunner()

            mExecutor!!.scheduleAtFixedRate(seekBarRunner!!, 0, 1000L, TimeUnit.MILLISECONDS)
        }

    }

    private fun pauseMedia() {
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
            resumePosition = mediaPlayer!!.currentPosition
            sendMediaState(Constants.MUSIC_ACTION_PAUSE)

        }
    }

    private fun resumeMedia() {
        if (mediaPlayer == null) return
        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.seekTo(resumePosition)
            mediaPlayer!!.start()
            startSendingProgress(true)
            sendMediaState(Constants.MUSIC_ACTION_PLAY)

        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        stopMedia()
        startSendingProgress(false)
        sendMediaState(Constants.MUSIC_ACTION_COMPLETED)

    }

    override fun onPrepared(mp: MediaPlayer?) {

        startSendingProgress(true)
        mp?.start()
        mp?.let {
            sendDurationBroadcast(it.duration)
            sendMediaState(Constants.MUSIC_ACTION_PLAY)

        }
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {

        sendMediaState(Constants.MUSIC_ACTION_TO_ACTIVITY_ERROR)
        startSendingProgress(false)
        Log.d(TAG, "onError: ")
        //Invoked when there has been an error during an asynchronous operation
        when (what) {
            MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> {
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK $extra")
            }
            MEDIA_ERROR_SERVER_DIED -> {
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED $extra")
            }
            MEDIA_ERROR_UNKNOWN -> {
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN $extra")
            }
        }

        return false
    }

    private fun sendProgressBroadcast(progress: Int) {
        val intentFilter = Intent(Constants.MUSIC_ACTION_TO_ACTIVITY)
        intentFilter.putExtra(Constants.MUSIC_ACTION_TO_ACTIVITY_PROGRESS, progress)
        sendBroadcast(intentFilter)
    }

    private fun sendBufferedBroadcast(progress: Int) {
        val intentFilter = Intent(Constants.MUSIC_ACTION_TO_ACTIVITY)
        intentFilter.putExtra(Constants.MUSIC_ACTION_TO_ACTIVITY_BUFFERRED, progress)
        sendBroadcast(intentFilter)
    }

    private fun sendDurationBroadcast(duration: Int) {
        val intentFilter = Intent(Constants.MUSIC_ACTION_TO_ACTIVITY)
        intentFilter.putExtra(Constants.MUSIC_ACTION_TO_ACTIVITY_DURATION, duration)
        sendBroadcast(intentFilter)
    }

    private fun sendMediaState(value: String) {
        val intent = Intent(Constants.MUSIC_ACTION_TO_ACTIVITY)
        intent.putExtra(Constants.MUSIC_ACTION_TO_ACTIVITY, value)
        sendBroadcast(intent)
    }

    private val becomingNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            pauseMedia()
            startSendingProgress(false)
        }
    }

    private fun registerBecomingNoisyReceiver() {
        //register after getting audio focus
        val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(becomingNoisyReceiver, intentFilter)
    }

    private val playActionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            intent?.let {
                val intExtra = it.getIntExtra(Constants.MUSIC_ACTION_SHEEK, -1)
                if (intExtra != -1) {
                    seekTo(intExtra)
                }

                when (it.getStringExtra(Constants.MUSIC_ACTION)) {
                    Constants.MUSIC_ACTION_PLAY -> {
                        //init media and update song
                        val stringExtraSong = it.getSerializableExtra(Constants.MUSIC_EXTRA_SONG)
                        if (stringExtraSong != null && stringExtraSong is MediaModel) {
                            currentSong = stringExtraSong
                            lastSongIndex = it.getIntExtra(Constants.MUSIC_EXTRA_LAST_INDEX, -1)
                            initMediaPlayer()
                        } else {
                            resumeMedia()
                        }
                    }

                    Constants.MUSIC_ACTION_PAUSE -> {
                        pauseMedia()
                    }

                    Constants.MUSIC_ACTION_STOP -> {
                        stopMedia()
                    }
                    Constants.MUSIC_ACTION_SHEEK -> {
                        val seekTo = it.getIntExtra(Constants.MUSIC_ACTION_SHEEK, 0)
                        seekTo(seekTo)
                    }

                }
            }

            //A PLAY_NEW_AUDIO action received
            //reset mediaPlayer to play the new Audio
            //stopMedia();
            //mediaPlayer.reset();
            //initMediaPlayer();
            //updateMetaData();
            //buildNotification(PlaybackStatus.PLAYING);
        }
    }

    private fun sendData() {
        Log.d(TAG, "sendData: should send position, duration,current index")
        val position = mediaPlayer?.currentPosition ?: -1
        val max = mediaPlayer?.duration ?: -1
        val playing = mediaPlayer != null && mediaPlayer!!.isPlaying
        val intent = Intent(Constants.MUSIC_ACTION_TO_ACTIVITY)

        intent.putExtra(Constants.MUSIC_ACTION_RESUMING, Constants.MUSIC_ACTION_RESUMING)
        intent.putExtra(Constants.MUSIC_ACTION_TO_ACTIVITY_DURATION, max)
        intent.putExtra(Constants.MUSIC_ACTION_SHEEK, position)
        intent.putExtra(Constants.BINDER_STATE, playing)
        intent.putExtra(Constants.MUSIC_EXTRA_LAST_INDEX, lastSongIndex)

        sendBroadcast(intent)
    }

    private fun registerPlayNewaudio() {
        //Register playNewMedia receiver
        val filter = IntentFilter(Constants.MUSIC_ACTION)
        registerReceiver(playActionReceiver, filter)
    }

    override fun onSeekComplete(mp: MediaPlayer?) {

        mp?.let { sendProgressBroadcast(mp.currentPosition) }
    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d(TAG, "onInfo: ")
        return false
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
        //Log.d(TAG, "onBufferingUpdate: $percent")
        sendBufferedBroadcast(percent)

    }

    override fun onAudioFocusChange(focusChange: Int) {
        //Invoked when the audio focus of the system is updated.
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                // resume playback
                if (mediaPlayer == null) initMediaPlayer() else if (!mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.start()
                    sendMediaState(Constants.MUSIC_ACTION_PLAY)
                }
                mediaPlayer!!.setVolume(1.0f, 1.0f)
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                // Lost focus for an unbounded amount of time: stop playback and release media player
                Log.d(TAG, "onAudioFocusChange: loss")
                sendMediaState(Constants.MUSIC_ACTION_STOP)
                if (mediaPlayer!!.isPlaying) mediaPlayer!!.stop()
                mediaPlayer!!.release()
                mediaPlayer = null
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {               // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                sendMediaState(Constants.MUSIC_ACTION_PAUSE)
                if (mediaPlayer!!.isPlaying) mediaPlayer!!.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->                 // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer!!.isPlaying) mediaPlayer!!.setVolume(0.1f, 0.1f)
        }
    }

    private fun requestAudioFocus(): Boolean {
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        val result = audioManager!!.requestAudioFocus(AudioFocusRequest.Builder(AudioManager.STREAM_MUSIC).build())
//        val result = audioManager!!.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
//                AudioManager.AUDIOFOCUS_GAIN)
        //Focus gained
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        //Could not gain focus
    }

    private fun removeAudioFocus(): Boolean {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager!!.abandonAudioFocus(this)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: start")
        intent?.let {
            when (it.getStringExtra(Constants.MUSIC_ACTION)) {
                Constants.MUSIC_ACTION_PLAY -> {
                    //init media and update song
                    val stringExtraSong = it.getSerializableExtra(Constants.MUSIC_EXTRA_SONG)

                    if (stringExtraSong != null && stringExtraSong is MediaModel) {
                        currentSong = stringExtraSong
                        lastSongIndex = it.getIntExtra(Constants.MUSIC_EXTRA_LAST_INDEX, -1)
                        initMediaPlayer()
                    } else {
                        playMedia()
                    }
                }

                Constants.MUSIC_ACTION_PAUSE -> {
                    pauseMedia()
                }

                Constants.MUSIC_ACTION_STOP -> {
                    stopMedia()
                }
                Constants.MUSIC_ACTION_SHEEK -> {
                    val seekTo = it.getIntExtra(Constants.MUSIC_ACTION_SHEEK, 0)
                    seekTo(seekTo)
                }

                Constants.MUSIC_ACTION_RESUMING -> {
                    resumeMedia()
                }

            }


            val etc = it.getStringExtra(Constants.MUSIC_ACTION_TO_ACTIVITY)
            if (etc != null && etc.contentEquals("SEND_ALL_DATA")) {
                Log.d(TAG, "onReceive: activity is resuming ")
                sendData()
            }
        }


        if (!requestAudioFocus()) {
            Log.d(TAG, "onStartCommand: stopping self")
            stopSelf()
        }


        return START_STICKY
    }

    private fun seekTo(seekTo: Int) {
        Utility.makeToast("Buffering...", applicationContext)
        mediaPlayer?.seekTo(seekTo)
        sendProgressBroadcast(seekTo)
    }


    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        super.onDestroy()
        mediaPlayer?.let {
            stopMedia()
            it.release()
        }

        removeAudioFocus()

        if (phoneStateListener != null)
            telephonyManager?.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)

        unregisterReceiver(becomingNoisyReceiver)
        unregisterReceiver(playActionReceiver)
    }

    //Handle incoming phone calls
    private fun callStateListener() {
        // Get the telephony manager
        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        //Starting listening for PhoneState changes
        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, incomingNumber: String) {
                when (state) {
                    TelephonyManager.CALL_STATE_OFFHOOK, TelephonyManager.CALL_STATE_RINGING -> if (mediaPlayer != null) {
                        pauseMedia()
                        ongoingCall = true
                    }
                    TelephonyManager.CALL_STATE_IDLE ->                         // Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false
                                resumeMedia()
                            }
                        }
                }
            }
        }
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager!!.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE)
    }

}