package com.cslori.echojournal.echos.presentation.components

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cslori.echojournal.core.presentation.designsystem.theme.EchoJournalTheme
import com.cslori.echojournal.core.presentation.designsystem.theme.MoodPrimary25
import com.cslori.echojournal.core.presentation.designsystem.theme.MoodPrimary35
import com.cslori.echojournal.core.presentation.designsystem.theme.MoodPrimary80
import com.cslori.echojournal.core.util.formatMMSS
import com.cslori.echojournal.echos.presentation.echos.models.PlayBackState
import com.cslori.echojournal.echos.presentation.models.MoodUi
import com.cslori.echojournal.echos.presentation.models.TrackSizeInfo
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun MoodPlayer(
    moodUi: MoodUi?,
    playBackState: PlayBackState,
    playerProgress: () -> Float,
    durationPlayed: Duration,
    totalPlaybackDuration: Duration,
    powerRatios: List<Float>,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onTrackSizeAvailable: (TrackSizeInfo) -> Unit,
    modifier: Modifier = Modifier,
    amplitudeBarWidth: Dp = 5.dp,
    amplitudeBarSpacing: Dp = 4.dp
) {
    val iconTint = when (moodUi) {
        null -> MoodPrimary80
        else -> moodUi.colorSet.vivid
    }
    val trackFillColor = when (moodUi) {
        null -> MoodPrimary80
        else -> moodUi.colorSet.vivid
    }
    val backgroundColor = when (moodUi) {
        null -> MoodPrimary25
        else -> moodUi.colorSet.faded
    }
    val trackColor = when (moodUi) {
        null -> MoodPrimary35
        else -> moodUi.colorSet.desaturated
    }

    val formattedDuration = remember(durationPlayed, totalPlaybackDuration) {
        "${durationPlayed.formatMMSS()}/${totalPlaybackDuration.formatMMSS()}"

    }
    Surface(
        shape = CircleShape,
        color = backgroundColor,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayBackButton(
                playBackState = playBackState,
                onPlayClick = onPlayClick,
                onPauseClick = onPauseClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = iconTint
                ),
            )
            PlayBar(
                amplitudeBarWidth = amplitudeBarWidth,
                amplitudeBarSpacing = amplitudeBarSpacing,
                powerRatios = powerRatios,
                trackColor = trackColor,
                trackFillColor = trackFillColor,
                playerProgress = playerProgress,
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        vertical = 10.dp,
                        horizontal = 8.dp
                    )
                    .fillMaxHeight()
            )

            Text(
                text = formattedDuration,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

@Preview
@Composable
private fun MoodPlayerPreview() {
    val ratios = remember {
        (1..30).map {
            Random.nextFloat()
        }
    }

    EchoJournalTheme {
        MoodPlayer(
            moodUi = MoodUi.NEUTRAL,
            playBackState = PlayBackState.PLAYING,
            playerProgress = { 0.34f },
            durationPlayed = 120.seconds,
            totalPlaybackDuration = 250.seconds,
            powerRatios = ratios,
            onPlayClick = {},
            onPauseClick = {},
            modifier = Modifier.fillMaxWidth(),
            onTrackSizeAvailable = {}
        )
    }
}