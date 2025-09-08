package com.cslori.echojournal.core.database

import androidx.collection.FloatList
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cslori.echojournal.core.database.echo.EchoDao
import com.cslori.echojournal.core.database.echo.EchoEntity
import com.cslori.echojournal.core.database.echo_topic_relation.EchoTopicCrossRef
import com.cslori.echojournal.core.database.topic.TopicEntity
import com.cslori.echojournal.echos.presentation.models.MoodUi

@Database(
    entities = [EchoEntity::class, TopicEntity::class, EchoTopicCrossRef::class],
    version = 1
)

@TypeConverters(MoodUi::class, FloatList::class)


abstract class EchoDatabase: RoomDatabase() {
    abstract val echoDao: EchoDao
}