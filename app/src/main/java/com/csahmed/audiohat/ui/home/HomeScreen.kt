package com.csahmed.audiohat.ui.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.core.net.toUri
import com.csahmed.audiohat.domain.model.Audio
import com.csahmed.audiohat.ui.home.component.AudioItem
import com.csahmed.audiohat.ui.home.component.CollapsedMusicView
import com.csahmed.audiohat.ui.home.sheet.SheetCollapsed
import com.csahmed.audiohat.ui.home.sheet.SheetContent
import com.csahmed.audiohat.ui.home.sheet.SheetExpanded
import com.csahmed.audiohat.ui.home.sheet.currentFraction
import com.csahmed.audiohat.ui.theme.AudioPlayerTheme
import kotlinx.coroutines.launch

 val fakeData = listOf(
    Audio(
        uri = "".toUri(),
        name = "csahmed.98.20@gmail.com",
        id = 0L,
        artist = "@CsAhmed2020",
        data = "",
        duration = 12345,
        title = "@CsAhmed2020"
    ),
     Audio(
         uri = "".toUri(),
         name = "csahmed.98.20@gmail.com",
         id = 0L,
         artist = "@CsAhmed2020",
         data = "",
         duration = 12345,
         title = "@CsAhmed2020"
     ),
     Audio(
         uri = "".toUri(),
         name = "csahmed.98.20@gmail.com",
         id = 0L,
         artist = "@CsAhmed2020",
         data = "",
         duration = 12345,
         title = "@CsAhmed2020"
     ),
     Audio(
         uri = "".toUri(),
         name = "csahmed.98.20@gmail.com",
         id = 0L,
         artist = "@CsAhmed2020",
         data = "",
         duration = 12345,
         title = "@CsAhmed2020"
     ),
     Audio(
         uri = "".toUri(),
         name = "csahmed.98.20@gmail.com",
         id = 0L,
         artist = "@CsAhmed2020",
         data = "",
         duration = 12345,
         title = "@CsAhmed2020"
     ),

    )


/*@ExperimentalCoilApi*/
@ExperimentalMotionApi
@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    progress: Float,
    onProgressChange: (Float) -> Unit,
    isAudioPlaying: Boolean,
    audioList: List<Audio>,
    onSearch:(query:String) -> Unit,
    query:String,
    time: String,
    currentPlayingAudio: Audio?,
    onStart: (Audio) -> Unit,
    onItemClick: (Audio) -> Unit,
    onNext: () -> Unit,
    onPrevious:() -> Unit,
    onFastForward:() -> Unit,
    onRewind:() -> Unit
) {


    val animatedHeight by animateDpAsState(
        targetValue = if (currentPlayingAudio == null) 0.dp else 90.dp
    )

    val scope = rememberCoroutineScope()

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(BottomSheetValue.Collapsed)
    )

    val sheetToggle: () -> Unit = {
        scope.launch {
            if (scaffoldState.bottomSheetState.isCollapsed) {
                scaffoldState.bottomSheetState.expand()
            } else {
                scaffoldState.bottomSheetState.collapse()
            }
        }
    }

    val radius = (30 * scaffoldState.currentFraction).dp

    val searchQuery = remember { mutableStateOf(query) }

    BottomSheetScaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        sheetShape = RoundedCornerShape(topStart = radius, topEnd = radius),
        sheetContent = {
            SheetContent {
                SheetExpanded(
                    currentFraction = scaffoldState.currentFraction
                ){
                    currentPlayingAudio?.let { currentPlayingAudio ->
                        AudioView(
                            progress = progress,
                            onProgressChange = onProgressChange,
                            audio = currentPlayingAudio,
                            isAudioPlaying = isAudioPlaying,
                            time = time,
                            onStart = { onStart.invoke(currentPlayingAudio) },
                            onNext = { onNext.invoke() },
                            onPrevious = {onPrevious.invoke()},
                            onRewind = {onRewind.invoke()},
                            onFastForward = {onFastForward.invoke()}
                        )
                    }
                }
                SheetCollapsed(
                    isCollapsed = scaffoldState.bottomSheetState.isCollapsed,
                    currentFraction = scaffoldState.currentFraction,
                    onSheetClick = sheetToggle
                ) {
                    currentPlayingAudio?.let { currentPlayingAudio ->
                        CollapsedMusicView(
                            isCollapsed = scaffoldState.bottomSheetState.isCollapsed,
                            progress = progress,
                            onProgressChange = onProgressChange,
                            audio = currentPlayingAudio,
                            isAudioPlaying = isAudioPlaying,
                            onStart = { onStart.invoke(currentPlayingAudio) },
                            onNext = { onNext.invoke() },
                            onPrevious = {onPrevious.invoke()}
                        )
                    }
                }
            }
        },
        sheetPeekHeight = animatedHeight
    ) {
        Column {
            OutlinedTextField(
                value = searchQuery.value,
                onValueChange = {
                    searchQuery.value = it
                    onSearch.invoke(searchQuery.value)
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                placeholder = {
                    Text(text = "Search")
                },
                maxLines = 1,
                singleLine = true
            )

            LazyColumn(
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(audioList) { audio: Audio ->
                    AudioItem(
                        audio = audio,
                        onItemClick = { onItemClick.invoke(audio)},
                    )
                }
            }

        }


    }

}

@Preview(showBackground = true)
@Composable
fun BottomBarPrev() {
    AudioPlayerTheme {
        CollapsedMusicView(
            progress = 50f,
            onProgressChange = {},
            audio = fakeData[0],
            isAudioPlaying = true,
            onStart = { /*TODO*/ },
        onPrevious = {},
        onNext = {},
        isCollapsed = true)
    }

}

/*@ExperimentalCoilApi*/
@ExperimentalMotionApi
@ExperimentalMaterialApi
@Preview(showSystemUi = true)
@Composable
fun HomeScreenPrev() {
    AudioPlayerTheme {
        HomeScreen(
            progress = 50f,
            onProgressChange = {},
            isAudioPlaying = true,
            audioList = fakeData,
            currentPlayingAudio = fakeData[0],
            onStart = {},
            onItemClick = {},
            onPrevious = {},
            onNext = {},
            onSearch = {},
            query = "",
            time = "",
            onFastForward = {},
            onRewind = {}
        )
    }

}























