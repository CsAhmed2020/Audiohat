package com.csahmed.audiohat.ui.home

import com.csahmed.audiohat.domain.model.Audio

sealed class AudiohatEvent{
    data class OnSearchQueryChange(val query: String): AudiohatEvent()
    object OnNextAudio :AudiohatEvent()
    object OnPreviousAudio :AudiohatEvent()
    object OnFastForward:AudiohatEvent()
    object OnRewind:AudiohatEvent()
    data class OnAudioClick(val audio: Audio) :AudiohatEvent()
    data class OnSliderClick(val value:Float):AudiohatEvent()

}
