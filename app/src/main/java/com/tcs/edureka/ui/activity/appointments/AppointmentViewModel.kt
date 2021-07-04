package com.tcs.edureka.ui.activity.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tcs.edureka.db.repository.AppointmentsRepository
import com.tcs.edureka.model.AppointmentDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author Bhuvaneshvar
 */
@HiltViewModel
class AppointmentViewModel @Inject constructor(private val dao: AppointmentsRepository)
    : ViewModel() {

    fun getAllAppointments() =
            dao.getAllAppointments()

    fun deleteAppointment(appointmentDataModel: AppointmentDataModel) =
            viewModelScope.launch {
                dao.deleteAppointment(appointmentDataModel)
            }

    fun addAppointment(appointmentDataModel: AppointmentDataModel) {
        viewModelScope.launch {
            dao.addReplaceAppointment(appointmentDataModel)
        }
    }

}