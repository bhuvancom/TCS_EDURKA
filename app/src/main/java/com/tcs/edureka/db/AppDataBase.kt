package com.tcs.edureka.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tcs.edureka.db.dao.AppointmentsDao
import com.tcs.edureka.model.AppointmentDataModel

@Database(entities = [AppointmentDataModel::class], version = 2, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {
    //todo add more doa if needed
    abstract fun getAppointmentDao(): AppointmentsDao


    companion object {
        @Volatile
        private var instance: AppDataBase? = null

        @JvmStatic
        fun getDatabase(context: Context): AppDataBase {
            val tempInstance = instance
            if (tempInstance != null) return tempInstance
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context,
                        AppDataBase::class.java,
                        "tcs_edureka.db")
                        .fallbackToDestructiveMigration()
                        .build()
                this.instance = instance
                return this.instance!!
            }
        }
    }
}