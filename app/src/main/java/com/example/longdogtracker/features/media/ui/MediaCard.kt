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
import com.example.longdogtracker.features.media.ui.model.UiEpisode
import com.example.longdogtracker.ui.theme.BlueyBodySnout

@Composable
fun MediaCard(
    uiEpisode: UiEpisode,
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
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    uiEpisode.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(16F, TextUnitType.Sp),
                    modifier = Modifier.semantics { heading() }
                )

                Row {
                    if (uiEpisode.longDogsFound > 0) {
                        LongDogWithCount(color = BlueyBodySnout, count = uiEpisode.longDogsFound)
                    }
                    if (uiEpisode.knownLongDogCount > 0 && uiEpisode.longDogsFound != uiEpisode.knownLongDogCount) {
                        LongDogWithCount(
                            color = Color.LightGray,
                            count = uiEpisode.knownLongDogCount - uiEpisode.longDogsFound
                        )
                    }
                    if (uiEpisode.knownLongDogCount == 0) {
                        LongDogWithCount(color = Color.Black, count = 0)
                    }
                }

            }

            Text(
                "Season: ${uiEpisode.season} Episode: ${uiEpisode.episode}",
                fontSize = TextUnit(16F, TextUnitType.Sp)
            )
            AsyncImage(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uiEpisode.imageUrl)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .crossfade(true)
                    .build(),
                contentScale = ContentScale.FillWidth,
                contentDescription = null,
            )
            Text(
                uiEpisode.description,
                fontSize = TextUnit(13F, TextUnitType.Sp)
            )
        }
    }
}

@Composable
private fun LongDogWithCount(color: Color, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.long_dog_black),
            colorFilter = ColorFilter.tint(color),
            modifier = Modifier.size(32.dp),
            contentDescription = null,
        )
        Text(
            text = "x$count",
            fontSize = TextUnit(13F, TextUnitType.Sp)
        )
    }
}