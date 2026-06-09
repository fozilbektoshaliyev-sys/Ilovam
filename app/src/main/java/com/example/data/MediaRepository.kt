package com.example.data

import android.content.Context
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class MediaRepository(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val dao = database.downloadedMediaDao()

    // Observes all active/completed local downloads
    val allDownloads: Flow<List<DownloadedMedia>> = dao.getAllDownloads()

    // Check if item is downloaded
    fun getDownloadById(id: String): Flow<DownloadedMedia?> = dao.getDownloadById(id)

    // Synchronous list of all media catalogs
    fun getMediaCatalog(): List<MediaItem> = MediaItem.mockData

    // Trigger dummy download that writes updates iteratively into the database
    suspend fun startSimulatedDownload(media: MediaItem, onProgressUpdate: (Int) -> Unit = {}) {
        // First check if it already exists or is completed
        val existing = dao.getDownloadById(media.id).firstOrNull()
        if (existing != null && existing.status == "COMPLETED") {
            return
        }

        // 1. Insert Initial Downloading State
        val downloadingItem = DownloadedMedia(
            id = media.id,
            title = media.title,
            type = media.type,
            imageUrl = media.imageUrl,
            videoUrl = media.videoUrl,
            fileSize = media.fileSize,
            duration = media.duration,
            description = media.description,
            progress = 0,
            status = "DOWNLOADING"
        )
        dao.insertDownload(downloadingItem)

        // 2. Loop to simulate real download bites
        var currentProgress = 0
        while (currentProgress < 100) {
            delay(1200) // update every 1.2s
            currentProgress += 10 + (0..15).random()
            if (currentProgress > 100) currentProgress = 100

            dao.updateDownloadProgress(media.id, currentProgress, "DOWNLOADING")
            onProgressUpdate(currentProgress)
        }

        // 3. Mark completed in DB
        dao.updateDownloadProgress(media.id, 100, "COMPLETED")
    }

    // Cancel / Delete a download
    suspend fun deleteDownload(id: String) {
        dao.deleteDownloadById(id)
    }

    // Clear everything
    suspend fun clearDownloads() {
        dao.clearAllDownloads()
    }
}
