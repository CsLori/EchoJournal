package com.cslori.echojournal.echos.presentation.echos.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cslori.echojournal.core.presentation.designsystem.chips.HashTagChip
import com.cslori.echojournal.core.presentation.designsystem.theme.EchoJournalTheme
import com.cslori.echojournal.core.util.defaultShadow
import com.cslori.echojournal.echos.presentation.components.MoodPlayer
import com.cslori.echojournal.echos.presentation.models.EchoUi
import com.cslori.echojournal.echos.presentation.models.MoodUi
import com.cslori.echojournal.echos.presentation.echos.models.TrackSizeInfo
import com.cslori.echojournal.echos.presentation.preview.PreviewModels

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EchoCard(
    echoUi: EchoUi,
    onTrackSizeAvailable: (TrackSizeInfo) -> Unit,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
            .defaultShadow(shape = RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = echoUi.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = echoUi.formattedRecordedAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            MoodPlayer(
                moodUi = echoUi.moodUi,
                playBackState = echoUi.playbackState,
                playerProgress = { echoUi.playbackRatio },
                durationPlayed = echoUi.playbackCurrentDuration,
                totalPlaybackDuration = echoUi.playbackTotalDuration,
                powerRatios = echoUi.amplitudes,
                onPlayClick = onPlayClick,
                onPauseClick = onPauseClick,
                onTrackSizeAvailable = onTrackSizeAvailable
            )

            if(!echoUi.note.isNullOrBlank()) {
                ExpendableText(echoUi.note)
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                echoUi.topics.forEach { topic ->
                    HashTagChip(text = topic)
                }
            }
        }
    }
}

@Preview
@Composable
private fun EchoCardPreview() {
    EchoJournalTheme {
        EchoCard(
            echoUi = PreviewModels.echoUi.copy(
                moodUi = MoodUi.EXCITED
            ),
            onTrackSizeAvailable = {},
            onPauseClick = {},
            onPlayClick = {}
        )
    }
}