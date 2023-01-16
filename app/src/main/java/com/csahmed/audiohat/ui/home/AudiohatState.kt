package com.csahmed.audiohat.ui.home

import com.csahmed.audiohat.domain.model.Audio

data class AudiohatState (
    val sounds: List<Audio> = emptyList(),
    val searchQuery: String = ""
)