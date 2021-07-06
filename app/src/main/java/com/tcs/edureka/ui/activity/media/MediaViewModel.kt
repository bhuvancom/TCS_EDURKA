package com.tcs.edureka.ui.activity.media

import com.tcs.edureka.db.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor(private val repo: MediaRepository) {
}