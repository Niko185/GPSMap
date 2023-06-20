package com.example.gpsmap.database.create

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gpsmap.database.entity.TrailModel
import com.example.gpsmap.database.interfacedb.Dao


// Create Database instance
@Database(entities = [TrailModel::class], version = 1)
abstract class MainDataBase() : RoomDatabase() {

    abstract fun getDao(): Dao

    companion object {
        @Volatile
         var INSTANCE: MainDataBase? = null
        fun getDataBase(context: Context): MainDataBase {
            return INSTANCE ?:

            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, MainDataBase::class.java, "gps_map.db").build()
                INSTANCE = instance
                return instance
            }
        }
    }


}