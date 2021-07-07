package com.tcs.edureka.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tcs.edureka.db.dao.AppointmentsDao
import com.tcs.edureka.db.dao.CallLogDao
import com.tcs.edureka.db.dao.MedialDao
import com.tcs.edureka.model.AppointmentDataModel
import com.tcs.edureka.model.CallLogModel
import com.tcs.edureka.model.mediaplayer.MediaModel

@Database(entities = [AppointmentDataModel::class, MediaModel::class,
    CallLogModel::class], version = 2, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {
    //todo add more doa if needed
    abstract fun getAppointmentDao(): AppointmentsDao
    abstract fun getMediaDao(): MedialDao
    abstract fun getCallLogDao(): CallLogDao

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