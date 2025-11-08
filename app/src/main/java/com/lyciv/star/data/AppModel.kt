package com.lyciv.star.data

import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.PrimaryKey

data class AppModel(
    val packageName: String,
    val appName: String,
    val icon: Drawable,
    val isFavorite: Boolean = false
)

@Entity(tableName = "favorites")
data class FavoriteApp(
    @PrimaryKey
    val packageName: String,
    val position: Int = 0
)
