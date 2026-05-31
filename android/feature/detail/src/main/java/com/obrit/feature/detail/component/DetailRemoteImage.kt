package com.obrit.feature.detail.component

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
internal fun DetailRemoteImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: @Composable BoxScope.() -> Unit = {},
) {
    var imageBitmap by remember(imageUrl) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(imageUrl) {
        imageBitmap = imageUrl?.takeIf { url -> url.isNotBlank() }?.loadImageBitmapOrNull()
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val bitmap = imageBitmap
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale,
            )
        } else {
            placeholder()
        }
    }
}

private suspend fun String.loadImageBitmapOrNull(): ImageBitmap? =
    runCatching {
        withContext(Dispatchers.IO) {
            URL(this@loadImageBitmapOrNull).openStream().use { stream ->
                BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }
        }
    }.getOrNull()
