package com.tcs.edureka.db.repository

import androidx.lifecycle.LiveData
import com.tcs.edureka.db.dao.CallLogDao
import com.tcs.edureka.model.CallLogModel
import javax.inject.Inject

class CallLogRepository @Inject constructor(private val callLogDao: CallLogDao) {
    suspend fun upsert(callLogModel: CallLogModel): Long = callLogDao.upsert(callLogModel)
    fun getAllCallLog(): LiveData<List<CallLogModel>> = callLogDao.getAllCallLog()
    suspend fun deleteCallLog(callLogModel: CallLogModel) = callLogDao.deleteCallLog(callLogModel)
}