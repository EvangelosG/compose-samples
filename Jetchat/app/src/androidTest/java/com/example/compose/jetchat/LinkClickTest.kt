/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.compose.jetchat

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import com.example.compose.jetchat.conversation.ClickableMessage
import com.example.compose.jetchat.conversation.ConversationContent
import com.example.compose.jetchat.conversation.Message
import com.example.compose.jetchat.conversation.SymbolAnnotationType
import com.example.compose.jetchat.conversation.messageFormatter
import com.example.compose.jetchat.data.exampleUiState
import com.example.compose.jetchat.theme.JetchatTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

private const val JetNewsUrl = "https://goo.gle/jetnews"
private const val DeveloperUrl = "https://developer.android.com"

/**
 * Records the URIs opened by the composables under test.
 */
private class RecordingUriHandler : UriHandler {
    var openedUri: String? = null
        private set

    override fun openUri(uri: String) {
        openedUri = uri
    }
}

/**
 * Checks that clicking a link in a chat message opens it through the [UriHandler].
 */
class LinkClickTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val uriHandler = RecordingUriHandler()

    @Test
    fun linkInMessage_click_opensUri() {
        setConversationContent()

        composeTestRule.onNodeWithText(JetNewsUrl, substring = true, useUnmergedTree = true)
            .performClickOnText(JetNewsUrl)

        assertEquals(JetNewsUrl, uriHandler.openedUri)
    }

    @Test
    fun linkOnlyMessage_click_opensUri() {
        setClickableMessage(DeveloperUrl)

        composeTestRule.onNodeWithText(DeveloperUrl, useUnmergedTree = true).performClick()

        assertEquals(DeveloperUrl, uriHandler.openedUri)
    }

    @Test
    fun plainTextInMessage_click_doesNotOpenUri() {
        setClickableMessage("Look at @aliconors profile, $DeveloperUrl")

        composeTestRule.onNodeWithText(DeveloperUrl, substring = true, useUnmergedTree = true)
            .performClickOnText("profile")

        assertNull(uriHandler.openedUri)
    }

    @Test
    fun messageFormatter_url_isAnnotatedAsLink() {
        lateinit var formatted: AnnotatedString
        composeTestRule.setContent {
            JetchatTheme {
                formatted = messageFormatter(text = "Read this: $DeveloperUrl", primary = false)
            }
        }

        val annotations = formatted.getStringAnnotations(
            tag = SymbolAnnotationType.LINK.name,
            start = 0,
            end = formatted.length,
        )

        assertEquals(1, annotations.size)
        assertEquals(DeveloperUrl, annotations.first().item)
    }

    private fun setConversationContent() {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalUriHandler provides uriHandler) {
                JetchatTheme {
                    ConversationContent(
                        uiState = exampleUiState,
                        navigateToProfile = { },
                        onNavIconPressed = { },
                    )
                }
            }
        }
    }

    private fun setClickableMessage(content: String) {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalUriHandler provides uriHandler) {
                JetchatTheme {
                    ClickableMessage(
                        message = Message(author = "Taylor Brooks", content = content, timestamp = "8:05 PM"),
                        isUserMe = false,
                        authorClicked = { },
                    )
                }
            }
        }
    }

    /**
     * Clicks in the middle of [substring] instead of the center of the node, so that the click
     * lands on the expected part of the message.
     */
    private fun SemanticsNodeInteraction.performClickOnText(substring: String) {
        val node = fetchSemanticsNode()
        val text = node.config[SemanticsProperties.Text].first()
        val index = text.indexOf(substring)
        check(index >= 0) { "'$substring' not found in '$text'" }

        val textLayoutResults = mutableListOf<TextLayoutResult>()
        node.config[SemanticsActions.GetTextLayoutResult].action?.invoke(textLayoutResults)
        val boundingBox = textLayoutResults.first().getBoundingBox(index + substring.length / 2)

        // Touch input coordinates are relative to the visible (clipped) bounds of the node, while
        // the bounding box is relative to the text layout.
        val clipOffset = node.positionInRoot - node.boundsInRoot.topLeft
        performTouchInput { click(boundingBox.center + clipOffset) }
    }
}
