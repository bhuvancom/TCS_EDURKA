package com.tcs.edureka.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tcs.edureka.model.AppointmentDataModel

/**
 * @author Bhuvaneshvar
 */
@Dao
interface AppointmentsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(appointmentDataModel: AppointmentDataModel): Long

    @Query("SELECT * FROM AppointmentDataModel order by year,month,date,hour,minute desc")
    fun getAllAppointments(): LiveData<List<AppointmentDataModel>>

    @Delete
    suspend fun deleteAppointment(appointmentDataModel: AppointmentDataModel)
}