package com.example.soporte.features.tickets.presentation.detail.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun SwipeToActionButton(
    text: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    icon: ImageVector = Icons.Default.ArrowForward,
) {
    val thumbSize = 56.dp
    val thumbPadding = 4.dp

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(CircleShape)
            .background(if (isLoading) containerColor.copy(alpha = 0.5f) else containerColor),
        contentAlignment = Alignment.CenterStart,
    ) {
        val width = constraints.maxWidth.toFloat()
        val thumbSizePx = with(LocalDensity.current) { thumbSize.toPx() }
        val thumbPaddingPx = with(LocalDensity.current) { thumbPadding.toPx() }
        val maxOffset = width - thumbSizePx - (thumbPaddingPx * 2)

        var swipeOffset by remember { mutableFloatStateOf(0f) }
        val animatedOffset by animateFloatAsState(
            targetValue = if (isLoading) maxOffset else swipeOffset,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            label = "swipe_offset",
        )

        Text(
            text = if (isLoading) "PROCESANDO..." else text,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor.copy(alpha = 0.9f),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = (thumbSize + 16.dp)),
        )

        Box(
            modifier = Modifier
                .padding(thumbPadding)
                .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                .size(thumbSize)
                .clip(CircleShape)
                .background(contentColor)
                .pointerInput(isLoading) {
                    if (isLoading) return@pointerInput
                    detectDragGestures(
                        onDragEnd = {
                            if (swipeOffset >= maxOffset * 0.95f) {
                                swipeOffset = maxOffset
                                onAction()
                            } else {
                                swipeOffset = 0f
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            swipeOffset = (swipeOffset + dragAmount.x).coerceIn(0f, maxOffset)
                        },
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = containerColor,
                    strokeWidth = 2.dp,
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = containerColor,
                )
            }
        }
    }
}
