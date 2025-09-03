package com.expensetracker.splashscreen

import android.app.Activity
import android.os.Build
import android.view.Window
import android.view.WindowInsetsController
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.expensetracker.R
import com.expensetracker.ui.theme.AppBackgroundColorDarkMode
import com.expensetracker.ui.theme.AppBackgroundColorLightMode
import com.expensetracker.viewmodel.ExpenseViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, viewModel: ExpenseViewModel) {
    val themeMode by viewModel.themeModeStateFlow.collectAsState()
    val isDarkMode = themeMode == 1

    val context = LocalContext.current
    val window = (context as Activity).window
    val statusBarColor = if (isDarkMode) AppBackgroundColorDarkMode else AppBackgroundColorLightMode
    SetSystemBars(window = window, statusBarColor, isDarkMode)

    val scale = remember {
        Animatable(0f)
    }
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 500,
                easing = {
                    OvershootInterpolator(2f).getInterpolation(it)
                }
            )
        )
        delay(500L)
        navController.navigate("main_screen")
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().background(statusBarColor)
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value)
        )
    }
}

@Composable
fun SetSystemBars(window: Window, statusBarColor: Color, isDarkMode: Boolean) {
    val systemUiController = rememberSystemUiController()

    LaunchedEffect(statusBarColor, isDarkMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) { // Android 15 and above
            // Android 15+ (edge-to-edge is default)
            WindowCompat.setDecorFitsSystemWindows(window, false)

            // Make system bars transparent
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()

            // Paint the background behind system bars with chosen color
            window.decorView.setBackgroundColor(statusBarColor.toArgb())
            ViewCompat.requestApplyInsets(window.decorView)

            // Set status bar icon colors
            window.insetsController?.setSystemBarsAppearance(
                if (isDarkMode) 0 else WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            // Set navigation bar icon colors
            window.insetsController?.setSystemBarsAppearance(
                if (isDarkMode) 0 else WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            )

        } else {
            // Android 14 and below (normal SystemUiController handling)
            systemUiController.setStatusBarColor(
                color = statusBarColor,
                darkIcons = !isDarkMode
            )
            systemUiController.setNavigationBarColor(
                color = statusBarColor,
                darkIcons = !isDarkMode
            )
        }
    }
}
