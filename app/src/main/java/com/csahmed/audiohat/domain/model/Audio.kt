package com.csahmed.audiohat.domain.model

import android.net.Uri

data class Audio(
    val id:Long,
    val uri: Uri,
    val name:String,
    val artist:String,
    val title:String,
    val data:String,
    val duration:Int
)
