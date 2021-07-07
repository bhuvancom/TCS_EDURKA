package com.tcs.edureka.ui.activity.media

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tcs.edureka.R
import com.tcs.edureka.db.repository.MediaRepository
import com.tcs.edureka.model.mediaplayer.MediaModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MediaViewModel"

/**
 * @author Bhuvaneshvar
 */
@HiltViewModel
class MediaViewModel @Inject constructor(private val repo: MediaRepository) : ViewModel() {


    fun getAllMedia() = repo.getMedia()


    fun addMedia(mediaModel: MediaModel) {
        Log.d(TAG, "addMedia: $mediaModel")
        viewModelScope.launch {
            repo.upsertMedia(mediaModel)
        }
    }

    fun deleteMedia(mediaModel: MediaModel) {
        viewModelScope.launch {
            repo.deleteMedia(mediaModel)
        }
    }

    fun setSong() {
        viewModelScope.launch { repo.deleteAll() }.invokeOnCompletion {
            if (repo.getMedia().value == null) {
                listOf(
                        MediaModel(
                                null, "https://hck.re/Rh8KTk",
                                R.drawable.p1,
                                "Afreen"),
                        MediaModel(
                                null, "https://hck.re/ZeSJFd",
                                R.drawable.p2,
                                "Aik Alif"),
                        MediaModel(
                                null, "https://hck.re/wxlUcX",
                                R.drawable.p3,
                                "Lovely"),
                        MediaModel(
                                null, "https://hck.re/H5nMm3",
                                R.drawable.p4,
                                "Afreen"),
                        MediaModel(
                                null, "https://hck.re/2nCncK",
                                R.drawable.p5,
                                "Tajder e haram"),
                        MediaModel(
                                null, "https://hck.re/epOzj9",
                                R.drawable.p6,
                                "Aaj Rung")
                ).forEach { media ->
                    viewModelScope.launch {
                        addMedia(media)
                    }
                }
            }
        }
    }
}