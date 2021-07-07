package com.tcs.edureka.ui.activity.media

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tcs.edureka.R
import com.tcs.edureka.databinding.ActivityMyMediaPlayerBinding
import com.tcs.edureka.model.mediaplayer.MediaModel
import com.tcs.edureka.services.MyMusicServiceKt
import com.tcs.edureka.utility.Constants
import com.tcs.edureka.utility.Utility
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "MyMediaPlayerActivity"

/**
 * @author Bhuvaneshvar
 */
@AndroidEntryPoint
class MyMediaPlayerActivity : AppCompatActivity() {
    lateinit var binding: ActivityMyMediaPlayerBinding
    lateinit var mediaViewModel: MediaViewModel
    private var current = -1
    private var next = 0
    private var previous = 0
    private var currentSongDuration = 0
    private var isPlaying = false
    private val isBuffering = !isPlaying
    private var nmbrOfsong = -1
    private var lastIndex = -1

    private val mediaAdapter by lazy {
        MyMediaAdapter {
            onSongSelected(it)
        }
    }

    private var userIsSeeking = false

    private var myMusicPlayerService: MyMusicServiceKt? = null
    private var serviceBound = false
    private var userAction: String? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MyMusicServiceKt.LocalBind
            myMusicPlayerService = binder.getService()
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
        }

    }

    override fun onStart() {
        Log.d(TAG, "onStart: called")
        super.onStart()
        bindServiceS(null, null)
        //val local = LocalBroadcastManager.getInstance(this)
        val intentFilter = IntentFilter(Constants.MUSIC_ACTION_TO_ACTIVITY)
        registerReceiver(Register(), intentFilter)

    }

    private fun playAudio(media: MediaModel, postion: Int) {
        media.payedCount += 1
        mediaViewModel.addMedia(media)

        if (!serviceBound) {
            bindServiceS(media, Constants.MUSIC_ACTION_PLAY, postion)
        } else {
            //Service is active
            //Send media with BroadcastReceiver
            sendState(Constants.MUSIC_ACTION_PLAY, media, postion)

        }
    }

    private fun bindServiceS(media: MediaModel?, action: String?, position: Int = -1) {
        val intent = Intent(this, MyMusicServiceKt::class.java)
        intent.putExtra(Constants.MUSIC_ACTION, action)
        intent.putExtra(Constants.MUSIC_EXTRA_SONG, media)
        intent.putExtra(Constants.MUSIC_EXTRA_LAST_INDEX, position)

        if (action == null) {
            Log.d(TAG, "bindServiceS: starting..")
            intent.putExtra(Constants.MUSIC_ACTION_TO_ACTIVITY, "SEND_ALL_DATA")
        }
        this.startService(intent)
        this.bindService(intent, serviceConnection, Context.BIND_IMPORTANT)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(Constants.BINDER_STATE, serviceBound)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        serviceBound = savedInstanceState.getBoolean(Constants.BINDER_STATE, false)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: called")
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: called")
        super.onDestroy()
//        if (serviceBound) {
//            unbindService(serviceConnection)
//            myMusicPlayerService?.stopSelf()
//        }

        //unregisterReceiver(registerForMusicUpdate)
    }

    private fun onSongSelected(mediaModel: Int) {

        if (mediaModel < 0) return

        current = mediaModel
        next = if (mediaModel == mediaAdapter.differ.currentList.size - 1) 0 else mediaModel + 1
        previous = if (mediaModel == 0) 0 else mediaModel - 1

        val get = mediaAdapter.differ.currentList[mediaModel]
        get?.let {
            playAudio(it, mediaModel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindServiceS(null, null)
        binding = ActivityMyMediaPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mediaViewModel = ViewModelProvider(this).get(MediaViewModel::class.java)

        userAction = intent.getStringExtra(Constants.MUSIC_ACTION)

        binding.mediaSeek.max = 0

        binding.mediaSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var userSelectedPo = 0
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    userSelectedPo = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                userIsSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                userIsSeeking = true
                //playerAdapter.seekTo(userSelectedPo)
                sendSeek(userSelectedPo)
            }

        })



        binding.recyclerViewMediaList.apply {
            layoutManager = LinearLayoutManager(this@MyMediaPlayerActivity,
                    LinearLayoutManager.VERTICAL, false)
            adapter = mediaAdapter
        }


        //mediaViewModel.setSong()
        mediaViewModel.getAllMedia().observe(this) {
            mediaAdapter.differ.submitList(it)
            it?.let {
                if (it.isEmpty()) {
                    Log.d(TAG, "onCreate: empty fill it")
                    mediaViewModel.setSong()
                } else {
                    nmbrOfsong = it.size
                }
            }
        }

        binding.apply {
            btnPlay.setOnClickListener {

                if (current == -1 && nmbrOfsong > 0) {
                    binding.recyclerViewMediaList.layoutManager?.getChildAt(0)?.performClick()
                } else {
                    if (!isPlaying) sendState(Constants.MUSIC_ACTION_PLAY)
                    else sendState(Constants.MUSIC_ACTION_PAUSE)
                }
            }

            btnNext.setOnClickListener {
                //onSongSelected(next)
                binding.recyclerViewMediaList.layoutManager?.getChildAt(next)?.performClick()
            }

            btnPrevious.setOnClickListener {
                //onSongSelected(previous)
                binding.recyclerViewMediaList.layoutManager?.getChildAt(previous)?.performClick()
            }
        }
    }

    private fun sendState(state: String, media: MediaModel? = null, position: Int = -1) {
        Log.d(TAG, "sendState: $state")
        val intent = Intent(Constants.MUSIC_ACTION)
        intent.putExtra(Constants.MUSIC_ACTION, state)
        intent.putExtra(Constants.MUSIC_EXTRA_SONG, media)
        intent.putExtra(Constants.MUSIC_EXTRA_LAST_INDEX, position)
        sendBroadcast(intent)
    }

    private fun sendSeek(seekTo: Int) {
        Log.d(TAG, "sendSeek: $seekTo")
        val intent = Intent(Constants.MUSIC_ACTION)
        intent.putExtra(Constants.MUSIC_ACTION_SHEEK, seekTo)
        sendBroadcast(intent)
    }

    inner class Register : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            context ?: return
            //Log.d(TAG, "onReceive: received")
            intent?.let {
                val state = it.getStringExtra(Constants.MUSIC_ACTION_TO_ACTIVITY)
                val progress = it.getIntExtra(Constants.MUSIC_ACTION_TO_ACTIVITY_PROGRESS, 0)
                val duration = it.getIntExtra(Constants.MUSIC_ACTION_TO_ACTIVITY_DURATION, 0)
                val buffer = it.getIntExtra(Constants.MUSIC_ACTION_TO_ACTIVITY_BUFFERRED, 0)

                //activity is resuming update content
                val resumming = it.getStringExtra(Constants.MUSIC_ACTION_RESUMING)

                if (resumming != null && resumming.contentEquals(Constants.MUSIC_ACTION_RESUMING)) {
                    val lastIndex = it.getIntExtra(Constants.MUSIC_EXTRA_LAST_INDEX, -1)

                    if (lastIndex > -1) {
                        Log.d(TAG, "onReceive: last index $lastIndex ")
                        current = lastIndex
                        next = if (lastIndex == mediaAdapter.differ.currentList.size) 0 else lastIndex + 1
                        previous = if (lastIndex == 0) 0 else lastIndex - 1

                        binding.recyclerViewMediaList.layoutManager?.getChildAt(current)
                                ?.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
                    }
                    val isPlayingK = it.getBooleanExtra(Constants.BINDER_STATE, false)
                    if (isPlayingK) {
                        val drawable = getDrawable(R.drawable.ic_baseline_pause_24)
                        binding.btnPlay.background = drawable
                        isPlaying = isPlayingK
                    } else {
                        isPlaying = false
                        val drawable = getDrawable(R.drawable.ic_baseline_play_circle_outline_24)
                        binding.btnPlay.background = drawable
                    }

                    val max = it.getIntExtra(Constants.MUSIC_ACTION_TO_ACTIVITY_DURATION, -1)
                    if (max > -1) {
                        binding.mediaSeek.max = max
                    }
                    val pos = it.getIntExtra(Constants.MUSIC_ACTION_SHEEK, -1)
                    if (pos > -1) binding.mediaSeek.progress = pos

                    handleUserSaid()
                }

                if (progress > 0) {
                    //  Log.d(TAG, "onReceive: $progress")
                    binding.mediaSeek.progress = progress
                }
                if (duration > 0) {
                    currentSongDuration = duration
                    binding.mediaSeek.max = duration
                }
                if (buffer > 0) {
                    val overall = (currentSongDuration * buffer / 100) as Int
                    binding.mediaSeek.secondaryProgress = overall
                }

                if (state != null) {

                    when (state) {
                        Constants.MUSIC_ACTION_PLAY -> {
                            isPlaying = true
                            val drawable = getDrawable(R.drawable.ic_baseline_pause_24)
                            binding.btnPlay.background = drawable
                        }

                        Constants.MUSIC_ACTION_PAUSE -> {
                            isPlaying = false
                            Log.d(TAG, "onReceive: paused")
                            val drawable = getDrawable(R.drawable.ic_baseline_play_circle_outline_24)
                            binding.btnPlay.background = drawable
                        }

                        Constants.MUSIC_ACTION_STOP -> {
                            isPlaying = false
                            val drawable = getDrawable(R.drawable.ic_baseline_play_circle_outline_24)
                            binding.btnPlay.background = drawable

                        }
                        Constants.MUSIC_ACTION_TO_ACTIVITY_ERROR -> {
                            Log.d(TAG, "onReceive: error $state")
                            val drawable = getDrawable(R.drawable.ic_baseline_play_circle_outline_24)
                            binding.btnPlay.background = drawable
                            isPlaying = false
                        }
                        Constants.MUSIC_ACTION_COMPLETED -> {
                            Log.d(TAG, "onReceive: Completed, play next")
                            Utility.makeToast("Playing next song", context)
                            onSongSelected(next)
                        }
                    }
                }

            }
        }

    }

    private fun handleUserSaid() {
        userAction ?: return
        Log.d(TAG, "handleUserSaid: $current,$nmbrOfsong,$userAction")
        when (userAction) {
            Constants.MUSIC_ACTION_PLAY -> {
                if (current == -1 && nmbrOfsong > 0) {
                    binding.recyclerViewMediaList.layoutManager?.getChildAt(0)?.performClick()
                } else {
                    if (!isPlaying) sendState(Constants.MUSIC_ACTION_PLAY)

                }
            }
            Constants.MUSIC_ACTION_PAUSE -> {
                sendState(Constants.MUSIC_ACTION_PAUSE)
            }
            Constants.MUSIC_ACTION_NEXT -> {
                binding.recyclerViewMediaList.layoutManager?.getChildAt(next)?.performClick()
            }

            Constants.MUSIC_ACTION_PREVIOUS -> {
                binding.recyclerViewMediaList.layoutManager?.getChildAt(previous)?.performClick()
            }
        }

        userAction = null
    }
}
