package com.tcs.edureka.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tcs.edureka.model.mediaplayer.MediaModel

/**
 * @author Bhuvaneshvar
 */
@Dao
interface MedialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(mediaModel: MediaModel): Long

    @Query("SELECT * FROM MediaModel order by payedCount desc")
    fun getAllMedia(): LiveData<List<MediaModel>>

    @Delete
    suspend fun deleteMedia(mediaModel: MediaModel)

    @Query("delete from MediaModel")
    suspend fun deleteAll()
}