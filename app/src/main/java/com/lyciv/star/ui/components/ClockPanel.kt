package com.lyciv.star.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lyciv.star.utils.TimeUtils
import kotlinx.coroutines.delay

@Composable
fun ClockPanel(modifier: Modifier = Modifier) {
    var currentTime by remember { mutableStateOf(TimeUtils.getCurrentTime()) }
    var currentDate by remember { mutableStateOf(TimeUtils.getCurrentDate()) }
    var timeColors by remember { mutableStateOf(TimeUtils.getTimeBasedColors()) }
    
    // Update time every second
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = TimeUtils.getCurrentTime()
            currentDate = TimeUtils.getCurrentDate()
            timeColors = TimeUtils.getTimeBasedColors()
            delay(1000L)
        }
    }
    
    // Animate color transitions
    val animatedPrimaryColor by animateColorAsState(
        targetValue = timeColors.primary,
        animationSpec = tween(durationMillis = 1000),
        label = "primary_color"
    )
    
    val animatedBlurColor by animateColorAsState(
        targetValue = timeColors.blur,
        animationSpec = tween(durationMillis = 1000),
        label = "blur_color"
    )
    
    Box(
        modifier = modifier
            .blur(20.dp)
            .background(
                color = animatedBlurColor,
                shape = RoundedCornerShape(32.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .blur(0.dp)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Time
                Text(
                    text = currentTime,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    color = animatedPrimaryColor,
                    letterSpacing = (-2).sp
                )
                
                // Date
                Text(
                    text = currentDate,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = animatedPrimaryColor.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
