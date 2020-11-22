package com.arpadfodor.communityparking.android.app.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.arpadfodor.communityparking.android.app.model.db.dataclasses.*

@Database(entities =
    [DbReport::class, DbUser::class, DbMetaData::class],
    version = 1, exportSchema = false)
abstract class ApplicationDB : RoomDatabase() {

    companion object {

        private const val APPLICATION_DB_NAME = "application_database"
        const val REPORT_TABLE_NAME = "report_table"
        const val USER_TABLE_NAME = "user_table"
        const val META_TABLE_NAME = "meta_table"

        // Singleton prevents multiple instances of database opening at the same time
        @Volatile
        private var INSTANCE: ApplicationDB? = null

        fun getDatabase(context: Context): ApplicationDB {

            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ApplicationDB::class.java,
                    APPLICATION_DB_NAME
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }

        }

    }

    abstract fun reportTable(): ReportDAO
    abstract fun userTable(): UserDAO
    abstract fun metaTable(): MetaDAO

}