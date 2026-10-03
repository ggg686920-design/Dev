package com.localdownloader.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.localdownloader.R
import com.localdownloader.domain.models.AccentPreset
import com.localdownloader.domain.models.ThemeMode
import com.localdownloader.ui.components.PreferenceItem
import com.localdownloader.ui.components.PreferencePageScaffold
import com.localdownloader.ui.components.PreferenceSubtitle
import com.localdownloader.ui.screens.settings.SettingChoiceDialog
import com.localdownloader.ui.screens.settings.SettingChoiceDialogState
import com.localdownloader.ui.screens.settings.SettingChoiceOption
import com.localdownloader.ui.screens.settings.appLanguageLabel
import com.localdownloader.ui.screens.settings.supportedAppLanguageOptions

@Composable
fun MoreScreen(
    currentLanguageTag: String,
    onLanguageSelected: (String) -> Unit,
    currentThemeMode: ThemeMode,
    currentAccentPreset: AccentPreset,
    onOpenAppearance: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var showLanguageDialog by remember { mutableStateOf(false) }

    val systemDefaultLabel = stringResource(R.string.common_system_default)
    val interfaceLanguageLabel = stringResource(R.string.common_interface_language)
    val appearanceLanguageTitle = stringResource(R.string.appearance_language_title)
    val appearanceLanguageSystemSubtitle = stringResource(R.string.appearance_language_system_subtitle)

    if (showLanguageDialog) {
        val currentLabel = appLanguageLabel(currentLanguageTag, systemDefaultLabel)
        val languageOptions = listOf(
            SettingChoiceOption(
                title = systemDefaultLabel,
                subtitle = appearanceLanguageSystemSubtitle,
                onSelect = {
                    showLanguageDialog = false
                    onLanguageSelected("")
                },
            ),
        ) + supportedAppLanguageOptions(interfaceLanguageLabel).map { option ->
            SettingChoiceOption(
                title = option.title,
                subtitle = option.subtitle,
                onSelect = {
                    showLanguageDialog = false
                    onLanguageSelected(option.tag)
                },
            )
        }

        SettingChoiceDialog(
            state = SettingChoiceDialogState(
                title = appearanceLanguageTitle,
                selected = currentLabel,
                options = languageOptions,
            ),
            onDismiss = { showLanguageDialog = false },
        )
    }

    PreferencePageScaffold(
        title = stringResource(R.string.more_title),
        onBack = null,
        modifier = modifier,
    ) {
        item {
            PreferenceSubtitle(text = stringResource(R.string.more_preferences_section))
        }
        item {
            PreferenceItem(
                icon = Icons.Rounded.Language,
                title = stringResource(R.string.appearance_language_title),
                description = appLanguageLabel(currentLanguageTag, systemDefaultLabel),
                onClick = { showLanguageDialog = true },
            )
        }
        item {
            PreferenceItem(
                icon = Icons.Rounded.Palette,
                title = stringResource(R.string.appearance_title),
                description = "${currentThemeMode.name} • ${currentAccentPreset.name}",
                onClick = onOpenAppearance,
            )
        }
    }
}
