package com.csahmed.audiohat.ui.home.component

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.csahmed.audiohat.R
import com.csahmed.audiohat.domain.model.Audio

@Composable
fun CollapsedMusicView(
    isCollapsed:Boolean,
    progress: Float,
    onProgressChange: (Float) -> Unit,
    audio: Audio,
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
    onPrevious:() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .paint(
                painter = painterResource(id = R.drawable.background_e),
                contentScale = ContentScale.FillBounds
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Log.d("Ahmed", "Audio: $audio")
            ArtistInfo(
                modifier = Modifier.fillMaxWidth(0.7F),
                audio = audio,
                isAudioPlaying = isAudioPlaying,
            )

            Box(modifier = Modifier.wrapContentWidth()) {
                MediaPlayerController(
                    isAudioPlaying = isAudioPlaying,
                    onStart = { onStart.invoke() },
                    onNext = { onNext.invoke() },
                    onPrevious = { onPrevious.invoke() },
                    space = 6.dp
                )
            }
        }
        Slider(
            value = progress,
            onValueChange = { onProgressChange.invoke(it) },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.primary,
                activeTrackColor = Color.White
            ),
            enabled = isCollapsed
        )
    }
}

@Composable
fun ArtistInfo(
    modifier: Modifier = Modifier,
    isAudioPlaying :Boolean,
    audio: Audio
) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        AnimatedView(isAudioPlaying = isAudioPlaying,
            resId = R.raw.music_playing , size = 30.dp
        )

        Spacer(modifier = Modifier.width(4.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = audio.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h6,
                overflow = TextOverflow.Clip,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1
            )
            Text(
                text = audio.artist,
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.subtitle1,
                overflow = TextOverflow.Clip,
                maxLines = 1
            )
        }
    }
}