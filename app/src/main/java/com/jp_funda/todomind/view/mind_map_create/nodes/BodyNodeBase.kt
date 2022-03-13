package com.jp_funda.todomind.view.mind_map_create.nodes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.jp_funda.todomind.R
import com.jp_funda.todomind.data.repositories.task.entity.Task
import com.jp_funda.todomind.data.repositories.task.entity.TaskStatus
import com.jp_funda.todomind.view.mind_map_create.MindMapCreateViewModel
import kotlin.math.roundToInt

@Composable
fun BodyNodeBase(
    modifier: Modifier = Modifier,
    task: Task,
    viewModel: MindMapCreateViewModel,
    circleSize: Dp,
    // text parameters
    fontSize: TextUnit,
    maxLines: Int,
    onClick: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current

    var offsetX by remember { mutableStateOf(task.x ?: 0f) }
    var offsetY by remember { mutableStateOf(task.y ?: 0f) }
    // Make task state to identify task in callbacks update
    var rememberedTask by remember { mutableStateOf(task) }

    // Update states before ui update
    offsetX = task.x ?: 0f
    offsetY = task.y ?: 0f
    rememberedTask = task

    val scale = viewModel.getScale()

    val circleColor = if (rememberedTask.statusEnum != TaskStatus.Complete) {
        rememberedTask.color?.let { Color(it) } ?: run { colorResource(id = R.color.teal_200) }
    } else {
        Color.DarkGray
    }
    val fontColor = if (rememberedTask.statusEnum != TaskStatus.Complete) {
        Color.White
    } else Color.LightGray

    Row(
        modifier = modifier
            .offset { IntOffset((offsetX * scale).roundToInt(), (offsetY * scale).roundToInt()) }
            .clip(RoundedCornerShape(1000.dp))
            .background(colorResource(id = R.color.deep_purple))
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { haptic.performHapticFeedback(HapticFeedbackType.LongPress) },
                    onDragEnd = {
                        rememberedTask.x = offsetX
                        rememberedTask.y = offsetY
                        viewModel.updateTask(rememberedTask)
                        viewModel.refreshView()
                    }
                ) { change, dragAmount ->
                    change.consumeAllChanges()
                    offsetX += dragAmount.x / (viewModel.getScale()) // Note: Referencing viewModel directory is needed
                    offsetY += dragAmount.y / (viewModel.getScale())
                }
            }
            .clickable { onClick() },
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_checkbox_unchecked),
            contentDescription = "Circle",
            tint = rememberedTask.color?.let { Color(it) } ?: colorResource(id = R.color.teal_200))
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = rememberedTask.title ?: "",
            maxLines = maxLines,
            fontSize = fontSize * scale,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = fontColor,
        )
    }
}