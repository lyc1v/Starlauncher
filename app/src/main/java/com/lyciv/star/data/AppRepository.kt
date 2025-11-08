package com.lyciv.star.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

class AppRepository(private val context: Context) {
    private val packageManager: PackageManager = context.packageManager
    
    private val database = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "star_launcher_db"
    ).build()
    
    private val favoriteDao = database.favoriteDao()
    
    private val _allApps = MutableStateFlow<List<AppModel>>(emptyList())
    val allApps: StateFlow<List<AppModel>> = _allApps.asStateFlow()
    
    val favoriteApps: Flow<List<AppModel>> = combine(
        favoriteDao.getAllFavorites(),
        _allApps
    ) { favorites, apps ->
        val favoritePackages = favorites.map { it.packageName }.toSet()
        apps.filter { it.packageName in favoritePackages }
            .sortedBy { app -> 
                favorites.find { it.packageName == app.packageName }?.position ?: 999 
            }
    }
    
    init {
        refreshApps()
    }
    
    fun refreshApps() {
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        
        val apps = packageManager.queryIntentActivities(intent, 0)
            .map { resolveInfo ->
                AppModel(
                    packageName = resolveInfo.activityInfo.packageName,
                    appName = resolveInfo.loadLabel(packageManager).toString(),
                    icon = resolveInfo.loadIcon(packageManager)
                )
            }
            .sortedBy { it.appName.lowercase() }
        
        _allApps.value = apps
    }
    
    suspend fun addToFavorites(packageName: String): Boolean = withContext(Dispatchers.IO) {
        val count = favoriteDao.getFavoriteCount()
        if (count >= 5) {
            return@withContext false
        }
        favoriteDao.insertFavorite(FavoriteApp(packageName, count))
        true
    }
    
    suspend fun removeFromFavorites(packageName: String) = withContext(Dispatchers.IO) {
        favoriteDao.removeFavorite(packageName)
    }
    
    suspend fun isFavorite(packageName: String): Boolean = withContext(Dispatchers.IO) {
        favoriteDao.isFavorite(packageName)
    }
    
    fun launchApp(packageName: String) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        launchIntent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(it)
        }
    }
}
