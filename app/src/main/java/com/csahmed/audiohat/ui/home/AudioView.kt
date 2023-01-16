package com.csahmed.audiohat.ui.home

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.content.Context
import android.graphics.Paint
import android.graphics.Path
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import com.csahmed.audiohat.R
import com.csahmed.audiohat.domain.model.Audio
import com.csahmed.audiohat.ui.home.component.AnimatedView
import com.csahmed.audiohat.ui.home.component.MediaPlayerController
import com.csahmed.audiohat.ui.theme.AudioPlayerTheme

@ExperimentalMotionApi
@Composable
fun AudioView(
    progress: Float,
    onProgressChange: (Float) -> Unit,
    audio: Audio,
    isAudioPlaying: Boolean,
    time: String,
    onStart: () -> Unit,
    onNext: () -> Unit,
    onPrevious:() -> Unit,
    onFastForward:() -> Unit,
    onRewind:() -> Unit
) {

    val shownImage = rememberSaveable { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val chooseShownImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            shownImage.value = it
        }
    }

    val digitFont = FontFamily(Font(R.font.digit_font, weight = FontWeight.Thin))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.background_c),
                contentScale = ContentScale.FillBounds
            )
    ) {

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(end = 10.dp)) {

                Card(
                modifier = Modifier
                    .fillMaxWidth(0.5F)
                    .requiredHeightIn(min = 150.dp, max = 300.dp)
                    .padding(horizontal = 5.dp, vertical = 20.dp)
                    .clickable {
                        chooseShownImage.launch("image/*")
                    },
                    shape = RoundedCornerShape(20.dp)
            ) {
                Image(
                    bitmap = if (shownImage.value == null ) ImageBitmap.imageResource(id = R.drawable.default_image) else getImage(
                        context = context,
                        uri = shownImage.value!!
                    ),
                    contentDescription = "header image",
                    contentScale = ContentScale.FillBounds
                )
            }

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)) {

                Canvas(
                    modifier = Modifier
                        .size(20.dp)
                ) {
                    drawIntoCanvas {
                        val textPadding = 20.dp.toPx()
                        val arcHeight = 120.dp.toPx()
                        val arcWidth = 150.dp.toPx()

                        val path = Path().apply {
                            addArc(0f, textPadding, arcWidth, arcHeight, 180f, 180f)
                        }
                        it.nativeCanvas.drawTextOnPath(
                            audio.artist,
                            path,
                            0f,
                            0f,
                            Paint().apply {
                                textSize = 18.sp.toPx()
                                textAlign = Paint.Align.CENTER
                                color = 0xffffffff.toInt()
                            }
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center) {
                    AnimatedView(isAudioPlaying = isAudioPlaying,
                        resId = R.raw.play_music , size = 150.dp
                    )
                }

            }
        }


        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)) {
            Text(
                text = audio.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h4,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(end = 10.dp)) {

            Row(modifier = Modifier
                .fillMaxWidth(0.5f)
            ) {
                MediaPlayerController(
                    isAudioPlaying = isAudioPlaying,
                    onStart = { onStart.invoke() },
                    onNext = { onNext.invoke() },
                    onPrevious = { onPrevious.invoke() },
                    space = 40.dp
                )
            }

            Row(modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
                IconButton(onClick = {
                    onRewind.invoke()
                }) {
                    Icon(imageVector = Icons.Default.FastRewind, contentDescription = "forward icon")
                }

                IconButton(onClick = {
                    onFastForward.invoke()
                }) {
                    Icon(imageVector = Icons.Default.FastForward, contentDescription = "forward icon")
                }

                Spacer(modifier = Modifier.width(20.dp))
                Text(text = time, fontFamily = digitFont, fontSize = 16.sp)
            }

        }

        Spacer(modifier = Modifier.height(10.dp))

        Slider(
            value = progress,
            onValueChange = { onProgressChange.invoke(it) },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.primary,
                activeTrackColor = Color.White

            )
        )

        var caption by remember { mutableStateOf("") }

        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)) {
            TextField(value = caption,
                onValueChange = {
                    caption = it },

                placeholder = {
                    Text(text = "Write here")
                },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    backgroundColor = Color.Transparent
                    ),
            textStyle =  MaterialTheme.typography.caption)
        }
    }
}


@ExperimentalMotionApi
@Preview(showBackground = true)
@Composable
fun Show() {
    AudioPlayerTheme {
        AudioView(
            progress = 50f,
            onProgressChange = {},
            audio = fakeData[0],
            isAudioPlaying = true,
            onStart = { /*TODO*/ },
            onPrevious = {},
            onNext = {},
        time = "",
        onFastForward = {},
        onRewind = {})
    }

}

@Composable
fun getImage(context:Context,uri:Uri): ImageBitmap {
    val bitmap : Bitmap = if (Build.VERSION.SDK_INT < 28) {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)

    } else {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
    return bitmap.asImageBitmap()
}

