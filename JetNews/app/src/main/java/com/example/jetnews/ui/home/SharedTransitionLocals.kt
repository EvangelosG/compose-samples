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

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier

/**
 * Composition locals used to thread [SharedTransitionScope] and
 * [AnimatedVisibilityScope] from [HomeRoute] down to the card
 * composables so they can participate in shared-element transitions.
 *
 * [LocalActivePostId] carries the post ID that is currently being
 * transitioned (i.e. the selected article). Only the card whose ID
 * matches participates in the shared-element animation, preventing
 * unrelated cards from being hidden during the transition.
 */
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
val LocalAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }
val LocalActivePostId = compositionLocalOf<String?> { null }

/**
 * Convenience modifier that applies [SharedTransitionScope.sharedBounds]
 * when the shared-transition scopes are available **and** this [postId]
 * matches the currently active post. Returns the receiver [Modifier]
 * unchanged for non-active cards, previews, or expanded screens.
 */
@Composable
fun Modifier.sharedPostBounds(postId: String): Modifier {
    val sharedScope = LocalSharedTransitionScope.current ?: return this
    val animScope = LocalAnimatedVisibilityScope.current ?: return this
    val activePostId = LocalActivePostId.current
    if (activePostId != null && activePostId != postId) return this
    return with(sharedScope) {
        this@sharedPostBounds.sharedBounds(
            sharedContentState = rememberSharedContentState(key = "post_container_$postId"),
            animatedVisibilityScope = animScope,
            renderInOverlayDuringTransition = false,
        )
    }
}
