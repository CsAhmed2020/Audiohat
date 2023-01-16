package com.csahmed.audiohat.ui.home.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*

@Composable
fun AnimatedView(
    isAudioPlaying :Boolean,
    resId:Int,
    size: Dp
){
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(resId)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isAudioPlaying,
        iterations = LottieConstants.IterateForever,
        speed = 1f,
        restartOnPlay = false
    )

    LottieAnimation(
        composition = composition,
        progress = progress,
        modifier = Modifier
            .size(size)
            .padding(start = 5.dp)
    )

}