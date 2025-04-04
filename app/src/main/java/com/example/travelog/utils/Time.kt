package com.example.travelog.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Time {
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun getEpochTime(): Long {
        return System.currentTimeMillis()
    }

    @JvmStatic
    fun formatEpochTime(epochTime: Long): String {
        return dateTimeFormat.format(Date(epochTime))
    }

    fun parseDateToEpoch(dateString: String): Long {
        return dateTimeFormat.parse(dateString)?.time ?: 0L
    }
}