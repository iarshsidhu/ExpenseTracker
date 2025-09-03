package com.expensetracker

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.expensetracker.appui.BottomSheetContent
import com.expensetracker.appui.CustomCalendar
import com.expensetracker.appui.ExpenseList
import com.expensetracker.appui.ExpenseToolbar
import com.expensetracker.splashscreen.SetSystemBars
import com.expensetracker.splashscreen.SplashScreen
import com.expensetracker.ui.theme.AppBackgroundColorDarkMode
import com.expensetracker.ui.theme.AppBackgroundColorLightMode
import com.expensetracker.ui.theme.ExpenseTrackerTheme
import com.expensetracker.ui.theme.RedDarkGradient
import com.expensetracker.ui.theme.RedLightGradient
import com.expensetracker.viewmodel.ExpenseViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Navigation()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            currentFocus?.let { view ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                view.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") {
            val viewModel: ExpenseViewModel = hiltViewModel()
            SplashScreen(navController = navController, viewModel = viewModel)
        }
        composable("main_screen") {
            val viewModel: ExpenseViewModel = hiltViewModel()
            val themeMode by viewModel.themeModeStateFlow.collectAsState()
            val isDarkMode = themeMode == 1

            val bottomSheetState = rememberStandardBottomSheetState(
                skipHiddenState = false // must be false to allow hide()
            )
            val bottomScaffoldState = rememberBottomSheetScaffoldState(
                bottomSheetState = bottomSheetState
            )
            val scope = rememberCoroutineScope()

            val statusBarColor = if (isDarkMode) {
                AppBackgroundColorDarkMode
            } else {
                AppBackgroundColorLightMode
            }

            val context = LocalContext.current
            val window = (context as Activity).window
            SetSystemBars(window = window, statusBarColor, isDarkMode)

            val activity = LocalActivity.current
            BackHandler {
                when (bottomSheetState.currentValue) {
                    SheetValue.Expanded -> {
                        scope.launch { bottomSheetState.hide() }
                    }

                    SheetValue.PartiallyExpanded -> {
                        activity?.finish()
                    }

                    else -> {
                        activity?.finish()
                    }
                }
            }

            var amountText by remember { mutableStateOf("") }
            var descriptionText by remember { mutableStateOf("") }

            ExpenseTrackerTheme(darkTheme = isDarkMode) {
                BottomSheetScaffold(
                    scaffoldState = bottomScaffoldState,
                    sheetPeekHeight = 0.dp,
                    sheetContainerColor = statusBarColor,
                    topBar = {
                        ExpenseToolbar(
                            isDarkMode = isDarkMode,
                            onToggleTheme = {
                                val newStatus = if (isDarkMode) 0 else 1
                                viewModel.updateThemeModeStatus(newStatus)
                            },
                            viewModel = viewModel
                        )
                    },
                    sheetContent = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .imePadding()
                        ) {
                            BottomSheetContent(
                                amountText = amountText,
                                descriptionText = descriptionText,
                                onAmountChange = { amountText = it },
                                onDescriptionChange = { descriptionText = it },
                                onSave = {
                                    val amount = amountText.toDoubleOrNull()
                                    if (amount != null && descriptionText.isNotEmpty()) {
                                        viewModel.insertExpense(
                                            descriptionText,
                                            amount,
                                            System.currentTimeMillis()
                                        )
                                        scope.launch { bottomSheetState.hide() }
                                        amountText = ""
                                        descriptionText = ""
                                    }
                                },
                                isDarkMode = isDarkMode
                            )
                        }
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(statusBarColor)
                            .padding(it) // important for padding under sheet
                            .imePadding(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CalendarScreen(viewModel, isDarkMode)
                        Spacer(modifier = Modifier.height(20.dp))
                        TotalExpense(viewModel)
                        Spacer(modifier = Modifier.height(20.dp))
                        AddExpenseButton {
                            scope.launch {
                                if (bottomScaffoldState.bottomSheetState.hasExpandedState) {
                                    bottomScaffoldState.bottomSheetState.expand()
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        ExpenseList(viewModel, isDarkMode)
                    }
                }
            }
        }
    }
}

@Composable
fun TotalExpense(viewModel: ExpenseViewModel) {
    val totalAmount by viewModel.totalAmountStateFlow.collectAsState()
    val selectedCurrency by viewModel.currencyStateFlow.collectAsState()

    val displayText = "${selectedCurrency.firstOrNull() ?: ""} ${String.format("%.2f", totalAmount)}"
    val fontSize = if (displayText.length > 12) 14.sp else if (displayText.length > 10) 16.sp else 18.sp

    Box(
        modifier = Modifier
            .size(120.dp)
            .shadow(
                elevation = 60.dp, // Increase for stronger ambient effect
                shape = CircleShape,
                ambientColor = RedLightGradient.copy(alpha = 0.9f), // Stronger ambient glow
                spotColor = RedDarkGradient.copy(alpha = 0.8f)      // Darker inner spot
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(RedDarkGradient, RedLightGradient)
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayText,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun AddExpenseButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        RedDarkGradient,
                        RedLightGradient
                    )
                )
            )
    ) {
        Text(
            text = "ADD EXPENSE",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(viewModel: ExpenseViewModel, isDarkMode: Boolean) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    LaunchedEffect(selectedDate) {
        viewModel.loadExpensesForDate(selectedDate)
    }

    CustomCalendar(
        selectedDate = selectedDate,
        onDateSelected = { date -> selectedDate = date },
        isDarkMode = isDarkMode
    )
}