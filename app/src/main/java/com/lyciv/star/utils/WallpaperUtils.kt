package com.lyciv.star.utils

import android.content.Context
import android.net.Uri

class WallpaperUtils(private val context: Context) {
    private val prefs = context.getSharedPreferences("star_launcher_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_WALLPAPER_URI = "wallpaper_uri"
    }
    
    fun saveWallpaperUri(uri: String?) {
        prefs.edit().putString(KEY_WALLPAPER_URI, uri).apply()
    }
    
    fun getWallpaperUri(): String? {
        return prefs.getString(KEY_WALLPAPER_URI, null)
    }
    
    fun clearWallpaper() {
        prefs.edit().remove(KEY_WALLPAPER_URI).apply()
    }
}
