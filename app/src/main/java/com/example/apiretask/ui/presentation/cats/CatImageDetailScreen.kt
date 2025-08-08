package com.example.apiretask.ui.presentation.cats

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay

@Composable
fun CatImageDetailScreen(imageId: String, imageUrl: String, onBack: (() -> Unit)? = null) {
    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    var scale by remember { mutableStateOf(1f) }
    var gestureEndTime by remember { mutableStateOf(0L) }

    // Reset scale after gesture ends
    LaunchedEffect(gestureEndTime) {
        if (gestureEndTime > 0) {
            delay(200) // Wait 200ms after last gesture
            scale = 1f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            if (onBack != null) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Fixed position zoomable image with auto-reset
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            // Update scale
                            scale = (scale * zoom).coerceIn(1f, 5f)
                            // Update gesture end time
                            gestureEndTime = System.currentTimeMillis()
                        }
                    }
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Cat image $imageId",
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale
                        )
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Cat Image",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "ID: $imageId",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = imageUrl,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.pointerInput(imageUrl) {
                    detectTapGestures(
                        onLongPress = {
                            clipboardManager.setText(AnnotatedString(imageUrl))
                            Toast.makeText(context, "Copied URL to clipboard!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                }
            )
        }
    }
}