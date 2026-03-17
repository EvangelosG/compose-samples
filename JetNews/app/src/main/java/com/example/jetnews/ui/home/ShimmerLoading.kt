/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews.ui.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

/**
 * Creates a [Brush] that produces a horizontal shimmer effect, sliding a
 * highlight band across the placeholder shapes in an infinite loop.
 */
@Composable
private fun shimmerBrush(): Brush {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.surface,
        MaterialTheme.colorScheme.surfaceVariant,
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateX by transition.animateFloat(
        initialValue = -300f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_translate",
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateX, 0f),
        end = Offset(translateX + 300f, 0f),
    )
}

/**
 * Placeholder that mirrors the [PostCardTop] layout: a large image area
 * followed by three text lines (title, author, date).
 */
@Composable
private fun ShimmerPostCardTop(brush: Brush, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Box(
            modifier = Modifier
                .heightIn(min = 180.dp)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(brush),
        )
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(22.dp)
                .clip(MaterialTheme.shapes.small)
                .background(brush),
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(14.dp)
                .clip(MaterialTheme.shapes.small)
                .background(brush),
        )
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .height(12.dp)
                .clip(MaterialTheme.shapes.small)
                .background(brush),
        )
    }
}

/**
 * Placeholder that mirrors the [PostCardSimple] layout: a small thumbnail
 * on the left with two text lines on the right.
 */
@Composable
private fun ShimmerPostCardSimple(brush: Brush, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .size(40.dp)
                .clip(MaterialTheme.shapes.small)
                .background(brush),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 10.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(16.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(brush),
            )
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .height(14.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(brush),
            )
        }
        Spacer(Modifier.width(48.dp))
    }
}

/**
 * Divider matching [PostListDivider].
 */
@Composable
private fun ShimmerDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 14.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
    )
}

/**
 * Full-screen shimmer placeholder shown while the post feed is loading.
 * The layout mimics the real feed: a section title, a highlighted card,
 * and several simple cards, all rendered as animated placeholder shapes.
 */
@Composable
fun ShimmerPostListPlaceholder(modifier: Modifier = Modifier) {
    val brush = shimmerBrush()
    Column(modifier = modifier) {
        // Section title placeholder
        Box(
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                .width(120.dp)
                .height(16.dp)
                .clip(MaterialTheme.shapes.small)
                .background(brush),
        )
        // Highlighted card
        ShimmerPostCardTop(brush)
        ShimmerDivider()
        // Simple cards
        repeat(4) {
            ShimmerPostCardSimple(brush)
            ShimmerDivider()
        }
    }
}
