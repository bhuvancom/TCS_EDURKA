package com.tcs.edureka.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tcs.edureka.model.mediaplayer.MediaModel

@Dao
interface MedialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(mediaModel: MediaModel): Long

    @Query("SELECT * FROM MediaModel")
    fun getAllMedia(): LiveData<List<MediaModel>>

    @Delete
    suspend fun deleteMedia(mediaModel: MediaModel)
}