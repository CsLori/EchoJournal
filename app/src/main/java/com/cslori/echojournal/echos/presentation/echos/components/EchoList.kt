@file:OptIn(ExperimentalFoundationApi::class)

package com.cslori.echojournal.echos.presentation.echos.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cslori.echojournal.core.presentation.designsystem.theme.EchoJournalTheme
import com.cslori.echojournal.core.util.UiText
import com.cslori.echojournal.echos.presentation.echos.models.DaySection
import com.cslori.echojournal.echos.presentation.echos.models.RelativePosition
import com.cslori.echojournal.echos.presentation.echos.models.TrackSizeInfo
import com.cslori.echojournal.echos.presentation.models.MoodUi
import com.cslori.echojournal.echos.presentation.preview.PreviewModels
import java.time.Instant
import java.time.ZonedDateTime

@Composable
fun EchoList(
    sections: List<DaySection>,
    onPlayClick: (echoId: Int) -> Unit,
    onPauseClick: () -> Unit,
    onTrackSizeAvailable: (TrackSizeInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp)
    ) {
        sections.forEachIndexed { sectionIndex, (dateHeader, echos) ->
            stickyHeader {
                if (sectionIndex > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = dateHeader.asString().uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            itemsIndexed(items = echos, key = { _, echo -> echo.id }) { index, echo ->
                TimeLineItem(
                    echoUi = echo,
                    relativePosition = when {
                        index == 0 && echos.size == 1 -> RelativePosition.SINGLE_ENTRY
                        index == 0 -> RelativePosition.FIRST
                        index == echos.lastIndex -> RelativePosition.LAST
                        else -> RelativePosition.IN_BETWEEN
                    },
                    modifier = modifier,
                    onPlayClick = { onPlayClick(echo.id) },
                    onPauseClick = onPauseClick,
                ) {
                    onTrackSizeAvailable(it)
                }
            }
        }
    }
}

@Preview
@Composable
private fun EchoListPreview() {
    EchoJournalTheme {
        val todaysEchos = remember {
            (1..3).map {
                PreviewModels.echoUi.copy(
                    id = it,
                    recordedAt = Instant.now()
                )
            }
        }

        val yesterdaysEchos = remember {
            (4..6).map {
                PreviewModels.echoUi.copy(
                    id = it,
                    moodUi = MoodUi.PEACEFUL,
                    recordedAt = ZonedDateTime.now().minusDays(1).toInstant()
                )
            }
        }

        val echosFromTwoDaysAgo = remember {
            (7..9).map {
                PreviewModels.echoUi.copy(
                    id = it,
                    moodUi = MoodUi.EXCITED,
                    recordedAt = ZonedDateTime.now().minusDays(2).toInstant()
                )
            }
        }

        val sections = remember {
            listOf(
                DaySection(
                    dateHeader = UiText.Dynamic("Today"),
                    echos = todaysEchos
                ),
                DaySection(
                    dateHeader = UiText.Dynamic("Yesterday"),
                    echos = yesterdaysEchos
                ),
                DaySection(
                    dateHeader = UiText.Dynamic("20/07/2025"),
                    echos = echosFromTwoDaysAgo
                ),
            )
        }
        EchoList(
            sections = sections,
            onPlayClick = {},
            onPauseClick = {},
            onTrackSizeAvailable = {}
        )
    }
}