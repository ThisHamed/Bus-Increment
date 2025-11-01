package org.jahangostar.busincreasement.data.db.room

import androidx.room.TypeConverter
import saman.zamani.persiandate.PersianDate

class DatabaseConverters {

    @TypeConverter
    fun fromTimeStamp(value: Long?): PersianDate? {
        return value?.let { PersianDate(value) }
    }

    @TypeConverter
    fun toTimeStamp(date: PersianDate?): Long? {
        return date?.time
    }

}