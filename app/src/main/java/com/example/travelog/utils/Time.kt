package com.example.travelog.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Time {
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun getEpochTime(): Long {
        return System.currentTimeMillis()
    }

    fun formatEpochTime(epochTime: Long): String {
        return dateTimeFormat.format(Date(epochTime))
    }
}