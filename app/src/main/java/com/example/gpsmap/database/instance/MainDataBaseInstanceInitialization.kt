package com.example.gpsmap.database.instance

import android.app.Application
import com.example.gpsmap.database.create.MainDataBase

// Initialization db in Application
class MainDataBaseInstanceInitialization : Application() {
    val dataBaseInstanceInitialization by lazy { MainDataBase.getDataBase(this) }
}