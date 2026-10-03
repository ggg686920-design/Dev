package com.localdownloader.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.localdownloader.R

enum class DuplicateFileAction {
    DOWNLOAD_NEW_COPY,
    OVERWRITE,
    KEEP_EXISTING,
    CANCEL,
}

@Composable
fun DuplicateFileDialog(
    fileName: String,
    onActionSelected: (DuplicateFileAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = { onActionSelected(DuplicateFileAction.CANCEL) },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
            )
        },
        title = {
            Text(
                text = stringResource(R.string.browser_duplicate_file_title),
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = stringResource(R.string.browser_duplicate_file_msg, fileName),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = { onActionSelected(DuplicateFileAction.DOWNLOAD_NEW_COPY) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(stringResource(R.string.browser_duplicate_rename))
                }
                OutlinedButton(
                    onClick = { onActionSelected(DuplicateFileAction.OVERWRITE) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Text(stringResource(R.string.browser_duplicate_overwrite))
                }
                TextButton(
                    onClick = { onActionSelected(DuplicateFileAction.KEEP_EXISTING) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.browser_duplicate_keep))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = { onActionSelected(DuplicateFileAction.CANCEL) }) {
                Text(stringResource(R.string.common_cancel))
            }
        },
        modifier = modifier,
    )
}
