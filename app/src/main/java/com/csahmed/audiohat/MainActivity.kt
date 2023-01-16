package com.csahmed.audiohat

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.csahmed.audiohat.ui.home.AudiohatEvent
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint
import com.csahmed.audiohat.ui.home.HomeViewModel
import com.csahmed.audiohat.ui.home.HomeScreen
import com.csahmed.audiohat.ui.theme.AudioPlayerTheme


/*@ExperimentalCoilApi*/
@ExperimentalMotionApi
@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AudioPlayerTheme {
                val permissionState = rememberPermissionState(
                    permission = Manifest.permission.READ_EXTERNAL_STORAGE
                )

                val lifecycleOwner = LocalLifecycleOwner.current

                DisposableEffect(key1 = lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            permissionState.launchPermissionRequest()
                        }

                    }
                    lifecycleOwner.lifecycle.addObserver(observer)


                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    if (permissionState.hasPermission) {

                        val homeViewModel = viewModel(modelClass = HomeViewModel::class.java)

                        val state = homeViewModel.state


                        HomeScreen(
                            progress = homeViewModel.currentAudioProgress.value,
                            onProgressChange = {
                                homeViewModel.onEvent(
                                    AudiohatEvent.OnSliderClick(it)
                                )
                            },
                            isAudioPlaying = homeViewModel.isAudioPlaying,
                            audioList = state.sounds,
                            onSearch = {
                                homeViewModel.onEvent(
                                    AudiohatEvent.OnSearchQueryChange(it)
                                     )
                            },
                            query = state.searchQuery,
                            time = homeViewModel.timer.value,
                            currentPlayingAudio = homeViewModel
                                .currentPlayingAudio.value,
                            onStart = {
                                homeViewModel.onEvent(
                                    AudiohatEvent.OnAudioClick(it)
                                )
                            },
                            onItemClick = {
                                homeViewModel.onEvent(
                                    AudiohatEvent.OnAudioClick(it)
                                )
                            },
                            onNext = {
                                homeViewModel.onEvent(
                                    AudiohatEvent.OnNextAudio
                                )
                            },
                            onPrevious = {
                                homeViewModel.onEvent(
                                    AudiohatEvent.OnPreviousAudio
                                )
                            },
                            onRewind = {
                                homeViewModel.onEvent(
                                    AudiohatEvent.OnRewind
                                )
                            },
                            onFastForward = {
                                homeViewModel.onEvent(
                                    AudiohatEvent.OnFastForward
                                )
                            }
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "Grant permission to get data")
                        }
                    }
                }
            }
        }
    }
}
