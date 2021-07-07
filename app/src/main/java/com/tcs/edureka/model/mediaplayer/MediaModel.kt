package com.tcs.edureka.model.mediaplayer

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class MediaModel(
        @PrimaryKey(autoGenerate = true)
        val id: Int?,
        val src: String,
        val imgId: Int,
        val title: String,
        var payedCount: Int = 0
) : Serializable
