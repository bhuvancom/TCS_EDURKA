package com.tcs.edureka.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CallLogModel(
        @PrimaryKey(autoGenerate = true)
        val id: Int? = null,
        val name: String,
        val number: Long,
        val type: String,
        val time: String
)