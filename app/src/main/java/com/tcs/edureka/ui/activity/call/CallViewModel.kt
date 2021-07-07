package com.tcs.edureka.ui.activity.call

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tcs.edureka.db.repository.CallLogRepository
import com.tcs.edureka.model.CallLogModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author Bhuvaneshvar
 */
@HiltViewModel
class CallViewModel @Inject constructor(private val callLogRepository: CallLogRepository) : ViewModel() {

    fun getAllCallLog() = callLogRepository.getAllCallLog()

    fun deleteCall(callLogModel: CallLogModel) {
        viewModelScope.launch {
            callLogRepository.deleteCallLog(callLogModel)
        }
    }

    fun upsertCall(callLogModel: CallLogModel) {
        viewModelScope.launch {
            callLogRepository.upsert(callLogModel)
        }
    }

}