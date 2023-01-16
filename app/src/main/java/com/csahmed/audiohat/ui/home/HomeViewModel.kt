package com.csahmed.audiohat.ui.home

import android.support.v4.media.MediaBrowserCompat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.csahmed.audiohat.domain.model.Audio
import com.csahmed.audiohat.data.repository.AudioRepository
import com.csahmed.audiohat.domain.constants.Constants
import com.csahmed.audiohat.domain.constants.timeStampToDuration
import com.csahmed.audiohat.domain.exoplayer.MediaPlayerServiceConnection
import com.csahmed.audiohat.domain.exoplayer.currentPosition
import com.csahmed.audiohat.domain.exoplayer.isPlaying
import com.csahmed.audiohat.domain.service.MediaPlayerService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AudioRepository,
    serviceConnection: MediaPlayerServiceConnection
) : ViewModel() {

    var state by mutableStateOf(AudiohatState())

    private var searchJob: Job? = null


    val currentPlayingAudio = serviceConnection.currentPlayingAudio

    private val isConnected = serviceConnection.isConnected

    private lateinit var rootMediaId: String

    private var currentPlayBackPosition by mutableStateOf(0L)
    private var updatePosition = true
    private val playbackState = serviceConnection.plaBackState
    val isAudioPlaying: Boolean
        get() = playbackState.value?.isPlaying == true

    private val subscriptionCallback = object
        : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            super.onChildrenLoaded(parentId, children)
        }
    }
    private val serviceConnection = serviceConnection.also {
        updatePlayBack()
    }

     private val currentDuration:Long
    get() = MediaPlayerService.currentDuration

    var currentAudioProgress = mutableStateOf(0f)

    var timer = mutableStateOf("")

    init {
        getAudiohat()
    }

    private fun getAudiohat() {
        viewModelScope.launch {
            state = if (state.searchQuery.lowercase().isEmpty()) state.copy(sounds = getAndFormatAudioData())
            else state.copy(sounds = getAndFormatAudioData().filter {
                it.name.contains(state.searchQuery,ignoreCase = true) || it.title.contains(state.searchQuery,ignoreCase = true)
            })

            isConnected.collect {
                if (it) {
                    rootMediaId = serviceConnection.rootMediaId
                    serviceConnection.plaBackState.value?.apply {
                        currentPlayBackPosition = position
                    }
                    serviceConnection.subscribe(rootMediaId, subscriptionCallback)
                }
            }
        }

    }

    private suspend fun getAndFormatAudioData(): List<Audio> {
        return repository.getAudioData().map {
            val displayName = it.name
            val artist = if (it.artist.contains("<unknown>")) "Unknown Artist" else it.artist
            it.copy(
                name = displayName,
                artist = artist
            )
        }


    }

    fun onEvent(event: AudiohatEvent) {
        when (event) {
            AudiohatEvent.OnNextAudio -> skipToNext()
            is AudiohatEvent.OnAudioClick -> playAudio(event.audio)
            AudiohatEvent.OnPreviousAudio -> backToPrevious()
            is AudiohatEvent.OnSearchQueryChange -> {
                state = state.copy(searchQuery = event.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500L)
                    getAudiohat()
                }
            }
            is AudiohatEvent.OnSliderClick -> seekTo(event.value)
            AudiohatEvent.OnFastForward -> fastForward()
            AudiohatEvent.OnRewind -> rewind()
        }
    }

    private fun playAudio(currentAudio: Audio) {
        serviceConnection.playAudio(state.sounds)
        if (currentAudio.id == currentPlayingAudio.value?.id) {
            if (isAudioPlaying) {
                serviceConnection.transportControl.pause()
            } else {
                serviceConnection.transportControl.play()
            }


        } else {
            serviceConnection.transportControl
                .playFromMediaId(
                    currentAudio.id.toString(),
                    null
                )
        }


    }


    private fun fastForward() {
        serviceConnection.fastForward()
    }

    private fun rewind() {
        serviceConnection.rewind()
    }

    private fun skipToNext() {
        serviceConnection.skipToNext()
    }

    private fun backToPrevious(){
        serviceConnection.backToPrevious()
    }

    private fun seekTo(value: Float) {
        serviceConnection.transportControl.seekTo(
            (currentDuration * value / 100f).toLong()
        )
    }

    private fun updatePlayBack() {
        viewModelScope.launch {
            val position = playbackState.value?.currentPosition ?: 0

            if (currentPlayBackPosition != position) {
                currentPlayBackPosition = position
            }

            if (currentDuration > 0) {
                currentAudioProgress.value = (
                        currentPlayBackPosition.toFloat()
                                / currentDuration.toFloat() * 100f
                        )

                timer.value = timeStampToDuration(serviceConnection.plaBackState.value?.currentPosition!!)
            }

            delay(Constants.PLAYBACK_UPDATE_INTERVAL)
            if (updatePosition) {
                updatePlayBack()
            }


        }


    }

    override fun onCleared() {
        super.onCleared()
        serviceConnection.unSubscribe(
            Constants.MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {}
        )
        updatePosition = false
    }


}




























