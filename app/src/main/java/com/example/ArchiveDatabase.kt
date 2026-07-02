package com.example

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

// --- Room Entities ---

@Entity(tableName = "bookmarks")
data class BookmarkedVerse(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val workIndex: Int,
    val verseIndex: Int,
    val userNotes: String = "",
    val bookmarkedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "practice_sessions")
data class PracticeSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val workIndex: Int,
    val verseIndex: Int,
    val completedLoops: Int,
    val timestamp: Long = System.currentTimeMillis()
)

// --- DAO Interface ---

@Dao
interface ArchiveDao {
    @Query("SELECT * FROM bookmarks ORDER BY bookmarkedAt DESC")
    fun getAllBookmarksFlow(): Flow<List<BookmarkedVerse>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkedVerse)

    @Query("DELETE FROM bookmarks WHERE workIndex = :workIndex AND verseIndex = :verseIndex")
    suspend fun removeBookmark(workIndex: Int, verseIndex: Int)

    @Query("SELECT * FROM bookmarks WHERE workIndex = :workIndex AND verseIndex = :verseIndex LIMIT 1")
    suspend fun getBookmark(workIndex: Int, verseIndex: Int): BookmarkedVerse?

    @Query("SELECT * FROM practice_sessions ORDER BY timestamp DESC")
    fun getAllSessionsFlow(): Flow<List<PracticeSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPracticeSession(session: PracticeSession)

    @Query("SELECT SUM(completedLoops) FROM practice_sessions WHERE workIndex = :workIndex AND verseIndex = :verseIndex")
    fun getCompletedLoopsFlow(workIndex: Int, verseIndex: Int): Flow<Int?>
}

// --- App Database ---

@Database(entities = [BookmarkedVerse::class, PracticeSession::class], version = 1, exportSchema = false)
abstract class ArchiveDatabase : RoomDatabase() {
    abstract fun dao(): ArchiveDao

    companion object {
        @Volatile
        private var INSTANCE: ArchiveDatabase? = null

        fun getDatabase(context: Context): ArchiveDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArchiveDatabase::class.java,
                    "malick_sy_archive_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// --- Repository ---

class ArchiveRepository(private val dao: ArchiveDao) {
    val bookmarksFlow: Flow<List<BookmarkedVerse>> = dao.getAllBookmarksFlow()
    val sessionsFlow: Flow<List<PracticeSession>> = dao.getAllSessionsFlow()

    suspend fun addBookmark(workIndex: Int, verseIndex: Int, notes: String = "") {
        val existing = dao.getBookmark(workIndex, verseIndex)
        if (existing != null) {
            dao.insertBookmark(existing.copy(userNotes = notes, bookmarkedAt = System.currentTimeMillis()))
        } else {
            dao.insertBookmark(BookmarkedVerse(workIndex = workIndex, verseIndex = verseIndex, userNotes = notes))
        }
    }

    suspend fun removeBookmark(workIndex: Int, verseIndex: Int) {
        dao.removeBookmark(workIndex, verseIndex)
    }

    suspend fun getBookmark(workIndex: Int, verseIndex: Int): BookmarkedVerse? {
        return dao.getBookmark(workIndex, verseIndex)
    }

    suspend fun savePracticeSession(workIndex: Int, verseIndex: Int, loops: Int) {
        dao.insertPracticeSession(PracticeSession(workIndex = workIndex, verseIndex = verseIndex, completedLoops = loops))
    }

    fun getLoopsFlow(workIndex: Int, verseIndex: Int): Flow<Int?> {
        return dao.getCompletedLoopsFlow(workIndex, verseIndex)
    }
}
