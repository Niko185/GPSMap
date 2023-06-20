package com.example.gpsmap.database.interfacedb

import androidx.room.*
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gpsmap.database.entity.TrailModel
import kotlinx.coroutines.flow.Flow


@Dao
interface Dao {
    @Query(value = "SELECT * FROM saved_trails")
    fun getAllSavedTrails(): Flow<List<TrailModel>>

    @Insert
    suspend fun insertTrailModelInDatabase(trailModel: TrailModel)
    @Delete
    suspend fun deleteTrailModelFromDatabase(trailModel: TrailModel)
}