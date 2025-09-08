package com.cslori.echojournal.core.database.echo

import androidx.room.TypeConverter
import com.cslori.echojournal.echos.presentation.models.MoodUi

class MoodUiTypeConverter {

    @TypeConverter
    fun fromMood(value: MoodUi): String {
        return value.name
    }

    @TypeConverter
    fun toMood(moodName: String): MoodUi {
        return MoodUi.valueOf(moodName)
    }
}