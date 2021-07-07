package com.tcs.edureka.di

import android.content.Context
import com.tcs.edureka.api.WeatherAPI
import com.tcs.edureka.db.AppDataBase
import com.tcs.edureka.db.dao.AppointmentsDao
import com.tcs.edureka.db.dao.CallLogDao
import com.tcs.edureka.db.dao.MedialDao
import com.tcs.edureka.db.repository.AppointmentsRepository
import com.tcs.edureka.db.repository.CallLogRepository
import com.tcs.edureka.db.repository.MediaRepository
import com.tcs.edureka.utility.RetroCreator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TCSAppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDataBase {
        return AppDataBase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun providesAppointmentDao(db: AppDataBase): AppointmentsDao {
        return db.getAppointmentDao()
    }

    @Provides
    @Singleton
    fun providesAppointmentReposiotry(dao: AppointmentsDao): AppointmentsRepository {
        return AppointmentsRepository(dao)
    }

    @Provides
    @Singleton
    fun providesWeatherApi(): WeatherAPI {
        return RetroCreator.getInstance().apiCallSerive
    }

    @Provides
    @Singleton
    fun providesMediaDao(db: AppDataBase): MedialDao {
        return db.getMediaDao()
    }

    @Provides
    @Singleton
    fun providesCallLogDao(db: AppDataBase): CallLogDao {
        return db.getCallLogDao()
    }

    @Provides
    @Singleton
    fun providesMediaRepo(dao: MedialDao): MediaRepository {
        return MediaRepository(dao)
    }


    @Provides
    @Singleton
    fun providesCallRepo(dao: CallLogDao): CallLogRepository {
        return CallLogRepository(dao)
    }
}