package com.example.dictionaryplusplus.data.local

import androidx.room.TypeConverter
import com.example.dictionaryplusplus.domain.model.MasteryStatus

class RoomConverters {
    @TypeConverter
    fun fromMasteryStatus(status: MasteryStatus): String {
        return status.name
    }

    @TypeConverter
    fun toMasteryStatus(status: String): MasteryStatus {
        return MasteryStatus.fromString(status)
    }
}
