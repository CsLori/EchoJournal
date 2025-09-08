package com.cslori.echojournal.core.database.echo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.cslori.echojournal.core.database.echo_topic_relation.EchoTopicCrossRef
import com.cslori.echojournal.core.database.echo_topic_relation.EchoWithTopics
import com.cslori.echojournal.core.database.topic.TopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EchoDao {
    @Query("SELECT * FROM echoentity ORDER BY recordedAt DESC")
    fun observeEchos(): Flow<List<EchoWithTopics>>

    @Query("SELECT * FROM topicentity ORDER BY topic ASC")
    fun observeTopics(): Flow<List<TopicEntity>>

    @Query(
        """
        SELECT * FROM topicentity
        WHERE topic LIKE '%' || :query || '%'
        ORDER BY topic ASC
    """
    )
    fun searchTopics(query: String): Flow<List<TopicEntity>>

    @Insert
    suspend fun insertEcho(echoEntity: EchoEntity): Long

    @Upsert
    suspend fun upsertTopic(topicEntity: TopicEntity)

    @Insert
    suspend fun insertEchoTopicCrossRef(crossRef: EchoTopicCrossRef)

    @Transaction
    suspend fun insertEchoWithTopics(echoWithTopics: EchoWithTopics) {
        val echoId = insertEcho(echoWithTopics.echo)

        echoWithTopics.topics.forEach { topic ->
            upsertTopic(topic)
            insertEchoTopicCrossRef(
                crossRef = EchoTopicCrossRef(
                    echoId = echoId.toInt(),
                    topic = topic.topic
                )
            )
        }
    }
}