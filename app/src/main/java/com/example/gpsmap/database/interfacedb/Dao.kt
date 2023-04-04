package com.example.gpsmap.database.interfacedb

import androidx.room.*
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gpsmap.database.entity.TrailModel
import kotlinx.coroutines.flow.Flow


@Dao
interface Dao {
    @Insert
    suspend fun insertTrailModelInDatabase(trailModel: TrailModel)

    @Query(value = "SELECT * FROM saved_trails")
    fun getAllSavedTrails(): Flow<List<TrailModel>>
}