package com.localdownloader.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.localdownloader.R
import com.localdownloader.domain.models.StreamType
import com.localdownloader.domain.models.VideoQuality

private val URL_REGEX = Regex("""https?://[^\s<>"]+""")

@Composable
fun BatchLinkImportDialog(
    onDismissRequest: () -> Unit,
    onQueueBatchLinks: (links: List<String>, streamType: StreamType, audioFormat: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var rawInputText by remember { mutableStateOf("") }
    val extractedLinks = remember { mutableStateListOf<String>() }
    var selectedStreamType by remember { mutableStateOf<StreamType>(StreamType.VIDEO_AUDIO) }
    var selectedAudioFormat by remember { mutableStateOf("mp3") }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val content = stream.bufferedReader().use { it.readText() }
                val matches = URL_REGEX.findAll(content).map { it.value }.toList()
                matches.forEach { if (!extractedLinks.contains(it)) extractedLinks.add(it) }
            }
        }
    }

    fun parseAndAddLinks(text: String) {
        val matches = URL_REGEX.findAll(text).map { it.value }.toList()
        matches.forEach {
            if (!extractedLinks.contains(it)) extractedLinks.add(it)
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.browser_batch_import_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                IconButton(onClick = onDismissRequest) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = stringResource(R.string.common_close))
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = stringResource(R.string.browser_batch_import_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                OutlinedTextField(
                    value = rawInputText,
                    onValueChange = { rawInputText = it },
                    placeholder = { Text("https://link1.com\nhttps://link2.com") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(14.dp),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = {
                            if (rawInputText.isNotBlank()) {
                                parseAndAddLinks(rawInputText)
                                rawInputText = ""
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Icon(imageVector = Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(stringResource(R.string.common_save))
                    }

                    OutlinedButton(
                        onClick = {
                            clipboardManager.getText()?.text?.let { text ->
                                parseAndAddLinks(text)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Icon(imageVector = Icons.Rounded.ContentPaste, contentDescription = null, modifier = Modifier.size(18.dp))
                    }

                    OutlinedButton(
                        onClick = { filePicker.launch(arrayOf("text/plain", "*/*")) },
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Icon(imageVector = Icons.Rounded.FileOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = selectedStreamType == StreamType.VIDEO_AUDIO,
                        onClick = { selectedStreamType = StreamType.VIDEO_AUDIO },
                        label = { Text("Video") },
                    )
                    FilterChip(
                        selected = selectedStreamType == StreamType.AUDIO_ONLY,
                        onClick = { selectedStreamType = StreamType.AUDIO_ONLY },
                        label = { Text("Audio (MP3)") },
                    )
                }

                Text(
                    text = stringResource(R.string.browser_links_found, extractedLinks.size),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .heightIn(max = 240.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    itemsIndexed(extractedLinks) { index, link ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Link,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = link,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f),
                                )
                                if (index > 0) {
                                    IconButton(
                                        onClick = {
                                            val item = extractedLinks.removeAt(index)
                                            extractedLinks.add(index - 1, item)
                                        },
                                        modifier = Modifier.size(28.dp),
                                    ) {
                                        Icon(imageVector = Icons.Rounded.ArrowUpward, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                }
                                if (index < extractedLinks.lastIndex) {
                                    IconButton(
                                        onClick = {
                                            val item = extractedLinks.removeAt(index)
                                            extractedLinks.add(index + 1, item)
                                        },
                                        modifier = Modifier.size(28.dp),
                                    ) {
                                        Icon(imageVector = Icons.Rounded.ArrowDownward, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }
                                }
                                IconButton(
                                    onClick = { extractedLinks.removeAt(index) },
                                    modifier = Modifier.size(28.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.DeleteOutline,
                                        contentDescription = stringResource(R.string.common_remove),
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (extractedLinks.isNotEmpty()) {
                        onQueueBatchLinks(extractedLinks.toList(), selectedStreamType, selectedAudioFormat)
                        onDismissRequest()
                    }
                },
                enabled = extractedLinks.isNotEmpty(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(imageVector = Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.size(8.dp))
                Text(stringResource(R.string.browser_start_all_downloads))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.common_cancel))
            }
        },
        modifier = modifier,
    )
}
