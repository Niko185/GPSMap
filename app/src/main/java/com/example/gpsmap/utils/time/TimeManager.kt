package com.example.gpsmap.utils.time

import java.text.SimpleDateFormat
import java.util.*
@Suppress("SimpleDateFormat")
object TimeManager {
    private val timeFormatter = SimpleDateFormat("HH:mm:ss")
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy - HH:mm")
    fun getTime(timeInMillis: Long): String {
        timeFormatter.timeZone = TimeZone.getTimeZone("UTC")

        val systemCalendar = Calendar.getInstance()
        systemCalendar.timeInMillis = timeInMillis
        return timeFormatter.format(systemCalendar.time)
    }
    fun getDate(): String {
        val systemCalendar = Calendar.getInstance()
        return dateFormatter.format(systemCalendar.time)
    }

}