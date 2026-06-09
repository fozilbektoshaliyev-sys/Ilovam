package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadedMediaDao {
    @Query("SELECT * FROM downloaded_media ORDER BY downloadedAt DESC")
    fun getAllDownloads(): Flow<List<DownloadedMedia>>

    @Query("SELECT * FROM downloaded_media WHERE id = :id LIMIT 1")
    fun getDownloadById(id: String): Flow<DownloadedMedia?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadedMedia)

    @Query("DELETE FROM downloaded_media WHERE id = :id")
    suspend fun deleteDownloadById(id: String)

    @Query("UPDATE downloaded_media SET progress = :progress, status = :status WHERE id = :id")
    suspend fun updateDownloadProgress(id: String, progress: Int, status: String)

    @Query("DELETE FROM downloaded_media")
    suspend fun clearAllDownloads()
}
