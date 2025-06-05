@file:OptIn(ExperimentalLayoutApi::class)

package com.cslori.echojournal.echos.presentation.echos.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.cslori.echojournal.R
import com.cslori.echojournal.core.presentation.designsystem.chips.MultiChoiceChip
import com.cslori.echojournal.core.presentation.designsystem.dropdowns.Selectable
import com.cslori.echojournal.core.presentation.designsystem.dropdowns.SelectableOptionsDropDownMenu
import com.cslori.echojournal.core.util.UiText
import com.cslori.echojournal.echos.presentation.echos.EchosAction
import com.cslori.echojournal.echos.presentation.echos.models.EchoFilterChip
import com.cslori.echojournal.echos.presentation.models.MoodChipContent
import com.cslori.echojournal.echos.presentation.models.MoodUi

@Composable
fun FilterRow(
    moodChipContent: MoodChipContent,
    hasActiveMoodFilters: Boolean,
    selectedEchoFilterChip: EchoFilterChip?,
    moods: List<Selectable<MoodUi>>,
    topicChipTitle: UiText,
    hasActiveTopicFilters: Boolean,
    topics: List<Selectable<String>>,
    onAction: (EchosAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var isExpanded by remember { mutableStateOf(false) }

    var dropDownOffset by remember { mutableStateOf(IntOffset.Zero) }

    val configuration = LocalConfiguration.current
    var maxDropDownHeight = (configuration.screenHeightDp * 0.3).dp

    FlowRow(
        modifier = modifier
            .padding(16.dp)
            .onGloballyPositioned(
                onGloballyPositioned = {
                    dropDownOffset = IntOffset(
                        x = 0,
                        y = it.size.height
                    )
                }
            ),
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        MultiChoiceChip(
            text = moodChipContent.title.asString(),
            modifier = modifier,
            onClick = {
                onAction(EchosAction.OnMoodChipClick)
                isExpanded = true
            },
            isClearVisible = hasActiveMoodFilters,
            onClearButtonClick = { onAction(EchosAction.OnRemoveFilters(EchoFilterChip.MOODS)) },
            isHighlighted = hasActiveMoodFilters || selectedEchoFilterChip == EchoFilterChip.MOODS,
            isDropDownVisible = selectedEchoFilterChip == EchoFilterChip.MOODS,
            dropDownMenu = {
                SelectableOptionsDropDownMenu(
                    items = moods,
                    itemDisplayText = { moodUi -> moodUi.title.asString(context) },
                    onItemClick = { moodUi -> onAction(EchosAction.OnFilterByMood(moodUi.item)) },
                    onDismiss = { onAction(EchosAction.OnDismissMoodDropdown) },
                    key = { moodUi -> moodUi.title },
                    leadingIcon = { moodUi ->
                        Image(
                            imageVector = ImageVector.vectorResource(moodUi.iconSet.fill),
                            contentDescription = moodUi.title.asString(),
                            modifier = Modifier.padding(8.dp)
                        )
                    },
                    dropDownOffset = dropDownOffset,
                    maxDropDownHeight = maxDropDownHeight,
                    dropDownExtras = TODO()
                )
            },
            leadingContent = {
                if (moodChipContent.iconsRes.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy((-4).dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        moodChipContent.iconsRes.forEach { iconRes ->
                            Image(
                                imageVector = ImageVector.vectorResource(iconRes),
                                contentDescription = moodChipContent.title.asString(),
                                modifier = Modifier
                                    .height(16.dp)
                            )
                        }
                    }
                }
            }
        )

        MultiChoiceChip(
            text = topicChipTitle.asString(),
            modifier = modifier,
            onClick = {
                onAction(EchosAction.OnTopicChipClick)
                isExpanded = true
            },
            isClearVisible = hasActiveTopicFilters,
            onClearButtonClick = { onAction(EchosAction.OnRemoveFilters(EchoFilterChip.TOPICS)) },
            isHighlighted = hasActiveTopicFilters || selectedEchoFilterChip == EchoFilterChip.TOPICS,
            isDropDownVisible = selectedEchoFilterChip == EchoFilterChip.TOPICS,
            dropDownMenu = {
                if (topics.isEmpty()) {
                    SelectableOptionsDropDownMenu(
                        items = listOf(
                            Selectable(
                                item = stringResource(R.string.you_don_t_have_any_topics_yet),
                                selected = false
                            )
                        ),
                        itemDisplayText = { it },
                        onItemClick = {},
                        onDismiss = { onAction(EchosAction.OnDismissTopicDropdown) },
                        key = { it },
                        dropDownOffset = dropDownOffset,
                        maxDropDownHeight = maxDropDownHeight,
                    )
                } else {
                    SelectableOptionsDropDownMenu(
                        items = topics,
                        itemDisplayText = { topic -> topic },
                        onItemClick = { topic -> onAction(EchosAction.OnFilterByTopic(topic.item)) },
                        onDismiss = { onAction(EchosAction.OnDismissTopicDropdown) },
                        key = { topic -> topic },
                        leadingIcon = { topic ->
                            Image(
                                imageVector = ImageVector.vectorResource(R.drawable.hashtag),
                                contentDescription = topic,
                            )
                        },
                        dropDownOffset = dropDownOffset,
                        maxDropDownHeight = maxDropDownHeight,
                    )
                }
            },
        )
    }
}