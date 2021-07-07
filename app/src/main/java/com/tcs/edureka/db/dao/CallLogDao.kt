package com.tcs.edureka.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tcs.edureka.model.CallLogModel

@Dao
interface CallLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(callLogModel: CallLogModel): Long

    @Query("SELECT * FROM CallLogModel order by id desc")
    fun getAllCallLog(): LiveData<List<CallLogModel>>

    @Delete
    suspend fun deleteCallLog(callLogModel: CallLogModel)
}
