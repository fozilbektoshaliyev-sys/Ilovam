package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_media")
data class DownloadedMedia(
    @PrimaryKey val id: String,
    val title: String,
    val type: String, // "Anime", "Donghua", "Film"
    val imageUrl: String,
    val videoUrl: String,
    val fileSize: String,
    val duration: String,
    val description: String,
    val downloadedAt: Long = System.currentTimeMillis(),
    val progress: Int = 100, // 0 to 100
    val status: String = "COMPLETED" // "DOWNLOADING", "COMPLETED", "PAUSED"
)
