package com.tcs.edureka.ui.activity.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tcs.edureka.db.repository.AppointmentsRepository
import javax.inject.Inject

class AppointmentVMProvider @Inject constructor(private val dao: AppointmentsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AppointmentViewModel(dao) as T
    }
}