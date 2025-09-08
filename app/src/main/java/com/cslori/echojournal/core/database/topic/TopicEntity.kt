package com.cslori.echojournal.core.database.topic

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TopicEntity(
    @PrimaryKey(autoGenerate = true)
    val topic: String,
)