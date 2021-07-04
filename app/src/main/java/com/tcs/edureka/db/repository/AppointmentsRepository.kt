package com.tcs.edureka.db.repository

import com.tcs.edureka.db.dao.AppointmentsDao
import com.tcs.edureka.model.AppointmentDataModel
import javax.inject.Inject

class AppointmentsRepository @Inject constructor(private val appointmentsDao: AppointmentsDao) {
    fun getAllAppointments() = appointmentsDao.getAllAppointments()
    suspend fun deleteAppointment(appointment: AppointmentDataModel) = appointmentsDao.deleteAppointment(appointment)
    suspend fun addReplaceAppointment(appointment: AppointmentDataModel) = appointmentsDao.upsert(appointment)

}