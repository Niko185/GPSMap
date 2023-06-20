package com.example.gpsmap.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity (tableName = "saved_trails")
data class TrailModel(

    @PrimaryKey(autoGenerate = true)
    val id: Int?,

    @ColumnInfo(name = "time_trail")
    val time: String,

    @ColumnInfo(name = "date_ended_trail")
    val date: String,

    @ColumnInfo(name = "average_velocity_trail")
    val averageVelocity: String,

    @ColumnInfo(name = "action_velocity_trail")
    val actionVelocity: String,

    @ColumnInfo(name = "distance_trail")
    val distance: String,

    @ColumnInfo(name = "geopoints_trail")
    val geoPoints: String,

): Serializable
