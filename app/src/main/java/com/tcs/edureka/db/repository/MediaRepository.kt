package com.tcs.edureka.db.repository

import com.tcs.edureka.db.dao.MedialDao
import com.tcs.edureka.model.mediaplayer.MediaModel
import javax.inject.Inject

class MediaRepository @Inject constructor(val dao: MedialDao) {
    suspend fun upsertMedia(mediaModel: MediaModel) = dao.upsert(mediaModel)
    fun getMedia() = dao.getAllMedia()
    suspend fun deleteMedia(mediaModel: MediaModel) = dao.deleteMedia(mediaModel)
    suspend fun deleteAll() = dao.deleteAll()
}