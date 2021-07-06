package com.tcs.edureka.model.mediaplayer

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MediaModel(
        @PrimaryKey(autoGenerate = true)
        val id: Int?,
        val src: String
)
