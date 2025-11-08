package com.lyciv.star.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.lyciv.star.data.AppModel
import com.lyciv.star.data.AppRepository
import com.lyciv.star.ui.components.AppList
import com.lyciv.star.ui.components.ClockPanel
import com.lyciv.star.ui.components.FavoriteBar
import com.lyciv.star.ui.components.SearchBar
import kotlinx.coroutines.launch

@Composable
fun StarHomeScreen(
    appRepository: AppRepository,
    wallpaperUri: String?,
    onWallpaperChange: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // State
    val allApps by appRepository.allApps.collectAsState()
    val favoriteApps by appRepository.favoriteApps.collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var showAppPicker by remember { mutableStateOf(false) }
    var selectedAppForDialog by remember { mutableStateOf<AppModel?>(null) }
    
    // Filter apps based on search
    val filteredApps = remember(allApps, searchQuery) {
        if (searchQuery.isEmpty()) {
            allApps
        } else {
            allApps.filter { 
                it.appName.contains(searchQuery, ignoreCase = true) 
            }
        }
    }
    
    // Wallpaper picker
    val wallpaperLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            onWallpaperChange(it.toString())
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        // Wallpaper background
        if (wallpaperUri != null) {
            Image(
                painter = rememberAsyncImagePainter(Uri.parse(wallpaperUri)),
                contentDescription = "Wallpaper",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1A237E),
                                Color(0xFF0D47A1),
                                Color(0xFF01579B)
                            )
                        )
                    )
            )
        }
        
        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.5f)
                        )
                    )
                )
        )
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            wallpaperLauncher.launch("image/*")
                        }
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            
            // Clock panel
            ClockPanel()
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Favorite bar
            FavoriteBar(
                favorites = favoriteApps,
                onAppClick = { packageName ->
                    appRepository.launchApp(packageName)
                },
                onAddClick = {
                    showAppPicker = true
                },
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Search bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // App list
            AppList(
                apps = filteredApps,
                onAppClick = { packageName ->
                    appRepository.launchApp(packageName)
                },
                onAppLongClick = { app ->
                    selectedAppForDialog = app
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // App picker dialog
        if (showAppPicker) {
            AppPickerDialog(
                apps = allApps,
                onDismiss = { showAppPicker = false },
                onAppSelected = { app ->
                    scope.launch {
                        val success = appRepository.addToFavorites(app.packageName)
                        if (!success) {
                            // Show toast or snackbar about limit reached
                        }
                        showAppPicker = false
                    }
                }
            )
        }
        
        // App options dialog
        selectedAppForDialog?.let { app ->
            AppOptionsDialog(
                app = app,
                onDismiss = { selectedAppForDialog = null },
                onAddToFavorites = {
                    scope.launch {
                        appRepository.addToFavorites(app.packageName)
                        selectedAppForDialog = null
                    }
                },
                onRemoveFromFavorites = {
                    scope.launch {
                        appRepository.removeFromFavorites(app.packageName)
                        selectedAppForDialog = null
                    }
                }
            )
        }
    }
}

@Composable
fun AppPickerDialog(
    apps: List<AppModel>,
    onDismiss: () -> Unit,
    onAppSelected: (AppModel) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select App") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                AppList(
                    apps = apps,
                    onAppClick = { packageName ->
                        apps.find { it.packageName == packageName }?.let { app ->
                            onAppSelected(app)
                        }
                    },
                    onAppLongClick = {},
                    modifier = Modifier.fillMaxSize()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AppOptionsDialog(
    app: AppModel,
    onDismiss: () -> Unit,
    onAddToFavorites: () -> Unit,
    onRemoveFromFavorites: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(app.appName) },
        text = {
            Column {
                Button(
                    onClick = onAddToFavorites,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add to Favorites")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onRemoveFromFavorites,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Remove from Favorites")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
