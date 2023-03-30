package com.example.gpsmap.utils.time

import java.text.SimpleDateFormat
import java.util.*

object TimeManager {
    private val timeFormatter = SimpleDateFormat("HH:mm:ss")

    fun getTime(timeInMillis: Long): String {
        timeFormatter.timeZone = TimeZone.getTimeZone("UTC")

        val systemCalendar = Calendar.getInstance()
        systemCalendar.timeInMillis = timeInMillis
        return timeFormatter.format(systemCalendar.time)
    }

}