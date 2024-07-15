package com.example.longdogtracker.features.media.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
    uiMedia: UiMedia,
    showMediaSheet: () -> Unit
) {

    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                showMediaSheet.invoke()
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(size = 16.dp)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.long_dog_black),
                        colorFilter = ColorFilter.tint(color),
                        modifier = Modifier.size(32.dp),
                        contentDescription = null,
                    )
                    Text(
                        text = "x${uiMedia.knownLongDogCount}",
                        fontSize = TextUnit(13F, TextUnitType.Sp)
                    )
                }
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
}