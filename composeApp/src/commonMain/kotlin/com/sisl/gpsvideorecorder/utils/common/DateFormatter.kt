package com.sisl.gpsvideorecorder.utils.common

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object DateFormatter {
    fun dateFormat(timeInMillis: Long, type: Int = 1): String {
        val instant = Instant.fromEpochMilliseconds(timeInMillis)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        val day = dateTime.dayOfMonth.toString().padStart(2, '0')
        val month = dateTime.monthNumber.toString().padStart(2, '0')
        val year = dateTime.year
        val hour = dateTime.hour.toString().padStart(2, '0')
        val minute = dateTime.minute.toString().padStart(2, '0')
        val second = dateTime.second.toString().padStart(2, '0')

        if (type == 1) {
            return "$day-$month-$year $hour:$minute:$second"
        }
        return "$day-$month-$year $hour:$minute:$second"
    }
}