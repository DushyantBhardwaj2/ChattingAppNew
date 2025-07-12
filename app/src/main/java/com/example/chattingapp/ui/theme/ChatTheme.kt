package com.example.chattingapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

object ChatTheme {
    val myMessageBackground: Color
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) ChatColors.DarkMyMessageBackground else ChatColors.LightMyMessageBackground

    val otherMessageBackground: Color
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) ChatColors.DarkOtherMessageBackground else ChatColors.LightOtherMessageBackground

    val myMessageText: Color
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) ChatColors.DarkMyMessageText else ChatColors.LightMyMessageText

    val otherMessageText: Color
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) ChatColors.DarkOtherMessageText else ChatColors.LightOtherMessageText

    val chatBackground: Color
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) ChatColors.DarkChatBackground else ChatColors.LightChatBackground

    val inputBackground: Color
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) ChatColors.DarkInputBackground else ChatColors.LightInputBackground

    val divider: Color
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) ChatColors.DarkDivider else ChatColors.LightDivider

    val headerBackground: Color
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) ChatColors.DarkHeaderBackground else ChatColors.LightHeaderBackground

    val profileIconBorder: Color
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) ChatColors.DarkProfileIconBorder else ChatColors.LightProfileIconBorder
}
