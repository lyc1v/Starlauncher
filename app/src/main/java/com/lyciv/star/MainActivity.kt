package com.lyciv.star

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import com.lyciv.star.data.AppRepository
import com.lyciv.star.ui.StarHomeScreen
import com.lyciv.star.ui.theme.StarLauncherTheme
import com.lyciv.star.utils.WallpaperUtils

class MainActivity : ComponentActivity() {
    private lateinit var appRepository: AppRepository
    private lateinit var wallpaperUtils: WallpaperUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize repositories
        appRepository = AppRepository(this)
        wallpaperUtils = WallpaperUtils(this)
        
        // Edge to edge
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            StarLauncherTheme {
                var wallpaperUri by remember { mutableStateOf(wallpaperUtils.getWallpaperUri()) }
                
                StarHomeScreen(
                    appRepository = appRepository,
                    wallpaperUri = wallpaperUri,
                    onWallpaperChange = { uri ->
                        wallpaperUtils.saveWallpaperUri(uri)
                        wallpaperUri = uri
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh app list when returning to launcher
        appRepository.refreshApps()
    }
}
