package com.nature.files.ui.preview

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.nature.files.data.FileItem
import com.nature.files.data.StorageProvider
import com.nature.files.ui.theme.BarkBrown
import com.nature.files.ui.theme.CanopyGreen
import com.nature.files.ui.theme.ForestFloorBackground
import com.nature.files.ui.theme.Spectral
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayer(
    fileItem: FileItem,
    storageProvider: StorageProvider,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var duration by remember { mutableStateOf(0L) }
    var currentTime by remember { mutableStateOf(0L) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var tempFile by remember { mutableStateOf<File?>(null) }

    LaunchedEffect(fileItem) {
        withContext(Dispatchers.IO) {
            val uri = if (fileItem.path.startsWith("/")) {
                Uri.fromFile(File(fileItem.path))
            } else {
                try {
                    val file = File(context.cacheDir, "temp_audio_${System.currentTimeMillis()}_${fileItem.name}")
                    storageProvider.getInputStream(fileItem.path).use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }
                    tempFile = file
                    Uri.fromFile(file)
                } catch (e: Exception) {
                    error = e.message
                    null
                }
            }

            if (uri != null) {
                withContext(Dispatchers.Main) {
                    val player = ExoPlayer.Builder(context).build().apply {
                        setMediaItem(MediaItem.fromUri(uri))
                        prepare()
                        playWhenReady = true
                        addListener(object : Player.Listener {
                            override fun onPlaybackStateChanged(state: Int) {
                                if (state == Player.STATE_READY) {
                                    duration = this@apply.duration
                                    isLoading = false
                                }
                            }
                            override fun onIsPlayingChanged(playing: Boolean) {
                                isPlaying = playing
                            }
                            override fun onPlayerError(e: androidx.media3.common.PlaybackException) {
                                error = e.message
                            }
                        })
                    }
                    exoPlayer = player
                }
            } else {
                isLoading = false
            }
        }
    }

    LaunchedEffect(exoPlayer, isPlaying) {
        while (isPlaying) {
            exoPlayer?.let {
                currentTime = it.currentPosition
                if (duration > 0) {
                    progress = currentTime.toFloat() / duration
                }
            }
            delay(500)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer?.release()
            tempFile?.delete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(fileItem.name, style = MaterialTheme.typography.titleLarge.copy(fontFamily = Spectral)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ForestFloorBackground,
                    titleContentColor = BarkBrown
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.MusicNote,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = CanopyGreen
            )
            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                CircularProgressIndicator(color = CanopyGreen)
            } else if (error != null) {
                Text(text = "Error: $error", color = MaterialTheme.colorScheme.error)
            } else {
                Slider(
                    value = progress,
                    onValueChange = {
                        progress = it
                        exoPlayer?.seekTo((it * duration).toLong())
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = CanopyGreen,
                        activeTrackColor = CanopyGreen
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(formatTime(currentTime), style = MaterialTheme.typography.labelSmall)
                    Text(formatTime(duration), style = MaterialTheme.typography.labelSmall)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    IconButton(onClick = { exoPlayer?.seekTo(currentTime - 10000) }) {
                        Icon(Icons.Default.Replay10, contentDescription = "Back 10s", modifier = Modifier.size(32.dp))
                    }

                    FloatingActionButton(
                        onClick = {
                            if (isPlaying) exoPlayer?.pause() else exoPlayer?.play()
                        },
                        containerColor = CanopyGreen,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    IconButton(onClick = { exoPlayer?.seekTo(currentTime + 10000) }) {
                        Icon(Icons.Default.Forward10, contentDescription = "Forward 10s", modifier = Modifier.size(32.dp))
                    }
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val seconds = (ms / 1000) % 60
    val minutes = (ms / (1000 * 60)) % 60
    return "%02d:%02d".format(minutes, seconds)
}
