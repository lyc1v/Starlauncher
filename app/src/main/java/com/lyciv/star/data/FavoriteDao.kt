package com.lyciv.star.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY position ASC")
    fun getAllFavorites(): Flow<List<FavoriteApp>>
    
    @Query("SELECT * FROM favorites ORDER BY position ASC")
    suspend fun getAllFavoritesSync(): List<FavoriteApp>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteApp)
    
    @Query("DELETE FROM favorites WHERE packageName = :packageName")
    suspend fun removeFavorite(packageName: String)
    
    @Query("SELECT COUNT(*) FROM favorites")
    suspend fun getFavoriteCount(): Int
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE packageName = :packageName)")
    suspend fun isFavorite(packageName: String): Boolean
}

@Database(entities = [FavoriteApp::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}
