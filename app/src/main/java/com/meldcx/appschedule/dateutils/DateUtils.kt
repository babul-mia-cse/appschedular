package com.meldcx.appschedule.dateutils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



object DateTimeUtils {
    fun convertMillisToDateTime(millis: Long, pattern: String = "yyyy-MM-dd hh:mm a"): String {
        val date = Date(millis)
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(date)
    }
}
