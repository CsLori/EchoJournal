package com.cslori.echojournal.echos.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.cslori.echojournal.R
import com.cslori.echojournal.core.presentation.designsystem.theme.EchoJournalTheme
import com.cslori.echojournal.core.presentation.designsystem.theme.Pause
import com.cslori.echojournal.core.util.defaultShadow
import com.cslori.echojournal.echos.presentation.echos.models.PlaybackState
import com.cslori.echojournal.echos.presentation.models.MoodUi

@Composable
fun PlayBackButton(
    playBackState: PlaybackState,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    colors: IconButtonColors,
    modifier: Modifier = Modifier,
) {
    FilledIconButton(
        onClick = {
            when (playBackState) {
                PlaybackState.PLAYING -> onPauseClick()
                PlaybackState.PAUSED,
                PlaybackState.STOPPED -> onPlayClick()
            }
        },
        colors = colors,
        modifier = modifier.defaultShadow(),
    ) {
        Icon(
            imageVector = when (playBackState) {
                PlaybackState.PLAYING -> Icons.Filled.Pause
                PlaybackState.PAUSED,
                PlaybackState.STOPPED -> Icons.Filled.PlayArrow
            },
            contentDescription = when (playBackState) {
                PlaybackState.PLAYING -> stringResource(id = R.string.playing)
                PlaybackState.PAUSED -> stringResource(id = R.string.paused)
                PlaybackState.STOPPED -> stringResource(id = R.string.stopped)
            }
        )
    }

}

@Preview
@Composable
private fun PlayBackButtonPreview() {
    EchoJournalTheme {
        PlayBackButton(
            playBackState = PlaybackState.PLAYING,
            onPlayClick = {},
            onPauseClick = {},
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MoodUi.SAD.colorSet.vivid
            )
        )
    }
}