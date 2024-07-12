package com.example.longdogtracker.features.media.ui

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.longdogtracker.R
import com.example.longdogtracker.features.media.ui.model.MediaType
import com.example.longdogtracker.features.media.ui.model.UiMedia
import com.example.longdogtracker.ui.theme.BlueyBodySnout

@Composable
fun MediaCard(
    uiMedia: UiMedia
) {
    var flip by remember { mutableStateOf(false) }
    var flipRotation by remember { mutableFloatStateOf(0f) }
    val animationSpec = tween<Float>(1000, easing = CubicBezierEasing(0.4f, 0.0f, 0.8f, 0.8f))

    Card(
        modifier = Modifier
            .requiredHeight(500.dp)
            .padding(8.dp)
            .graphicsLayer {
                rotationY = flipRotation
                cameraDistance = 64 * density
            }
            .clickable {
                flip = !flip
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(size = 16.dp)
    ) {
        if (flipRotation < 90f) {
            MediaCardFrontContent(
                uiMedia
            )
        } else {
            MediaCardBackContent(uiMedia)
        }

    }

    LaunchedEffect(key1 = flip) {
        if (flip) {
            animate(
                initialValue = 0f,
                targetValue = 180f,
                animationSpec = animationSpec
            ) { value: Float, _: Float ->
                flipRotation = value
            }
        } else {
            animate(
                initialValue = flipRotation,
                targetValue = 0f,
                animationSpec = animationSpec
            ) { value: Float, _: Float ->
                flipRotation = value
            }
        }
    }
}

@Composable
private fun MediaCardFrontContent(
    uiMedia: UiMedia
) {
    Column(Modifier.padding(16.dp)) {
        val color = when {
            uiMedia.longDogsFound > 0 -> BlueyBodySnout
            uiMedia.knownLongDogCount > 0 && uiMedia.longDogsFound == 0 -> Color.LightGray
            else -> Color.Black
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                uiMedia.title,
                fontWeight = FontWeight.Bold,
                fontSize = TextUnit(16F, TextUnitType.Sp),
                modifier = Modifier.semantics { heading() }
            )
            Image(
                painter = painterResource(id = R.drawable.long_dog_black),
                colorFilter = ColorFilter.tint(color),
                modifier = Modifier.size(32.dp),
                contentDescription = null,
            )
        }
        if (uiMedia.type is MediaType.Show) {
            Text(
                "Season: ${uiMedia.type.season} Episode: ${uiMedia.type.episode}",
                fontSize = TextUnit(16F, TextUnitType.Sp)
            )
        }
        AsyncImage(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            model = ImageRequest.Builder(LocalContext.current)
                .data(uiMedia.imageUrl)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .crossfade(true)
                .build(),
            contentScale = if (uiMedia.type is MediaType.Show) ContentScale.FillWidth else ContentScale.Fit,
            contentDescription = null,
        )
        Text(
            uiMedia.description,
            fontSize = TextUnit(13F, TextUnitType.Sp)
        )
    }
}

@Composable
private fun MediaCardBackContent(
    uiMedia: UiMedia
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .graphicsLayer {
                rotationY = 180f
            },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        var text by remember { mutableStateOf(uiMedia.longDogLocation ?: "") }
        Text(
            text = "Long Dog Status",
            Modifier.semantics { heading() },
            fontWeight = FontWeight.Bold
        )
        Row(verticalAlignment = Alignment.CenterVertically) {

            val color = when {
                uiMedia.longDogsFound > 0 -> BlueyBodySnout
                uiMedia.knownLongDogCount > 0 && uiMedia.longDogsFound == 0 -> Color.LightGray
                else -> Color.Black
            }
            val longDogStatus = when {
                uiMedia.longDogsFound > 0 -> "Found"
                uiMedia.knownLongDogCount > 0 && uiMedia.longDogsFound == 0 -> "Not Found"
                else -> "Unknown"
            }
            LongDogStepper(count = uiMedia.longDogsFound) { count ->
                {}
            }
        }

        Text(
            text = "Long Dog Location",
            Modifier.semantics { heading() },
            fontWeight = FontWeight.Bold
        )
        TextField(
            value = text,
            onValueChange = {
                text = it
                { }
            },
        )
    }
}