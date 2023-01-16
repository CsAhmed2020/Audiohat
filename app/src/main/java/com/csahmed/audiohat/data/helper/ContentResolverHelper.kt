package com.csahmed.audiohat.data.helper

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
import com.csahmed.audiohat.domain.model.Audio
import javax.inject.Inject

class ContentResolverHelper @Inject
constructor(@ApplicationContext val context: Context) {
    private var cursor: Cursor? = null

    private val projection: Array<String> = arrayOf(
        MediaStore.Audio.AudioColumns._ID,
        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
        MediaStore.Audio.AudioColumns.ARTIST,
        MediaStore.Audio.AudioColumns.DATA,
        MediaStore.Audio.AudioColumns.DURATION,
        MediaStore.Audio.AudioColumns.TITLE,
    )

    private var selectionClause: String? = "${MediaStore.Audio.AudioColumns.IS_MUSIC} = ?"
    private var selectionArg = arrayOf("1") // IS_MUSIc == one >> true

    private val sortOrder = "${MediaStore.Audio.AudioColumns.DISPLAY_NAME} ASC"


    @WorkerThread
    fun getAudioData(): List<Audio> {
        return getCursorData()
    }


    private fun getCursorData(): MutableList<Audio> {
        val audioList = mutableListOf<Audio>()

        cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selectionClause,
            selectionArg,
            sortOrder
        )

       // Bitmap image = ThumbnailUtils.createAudioThumbnail("your file name", MediaStore.Images.Thumbnails.MICRO_KIND)

        cursor?.use { myCursor ->
            val idColumn =
                myCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
            val displayNameColumn =
                myCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
            val artistColumn =
                myCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
            val titleColumn =
                myCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
            val dataColumn =
                myCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
            val durationColumn =
                myCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)

            myCursor.apply {
                if (count == 0) {
                    Log.e("Cursor", "getCursorData: Cursor is Empty")
                } else {
                    while (myCursor.moveToNext()) {
                        val displayName = getString(displayNameColumn)
                        val id = getLong(idColumn)
                        val artist = getString(artistColumn)
                        val title = getString(titleColumn)
                        val data = getString(dataColumn)
                        val duration = getInt(durationColumn)

                        val uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        audioList.add(
                            Audio(
                                id = id,
                                uri = uri,
                                name = displayName,
                                artist = artist,
                                title = title,
                                data = data,
                                duration = duration

                            )
                        )
                    }
                }
            }
        }
        return audioList
    }
}