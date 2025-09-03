package com.expensetracker.appui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expensetracker.R
import com.expensetracker.ui.theme.ListBackgroundColorDarkMode
import com.expensetracker.ui.theme.ListBackgroundColorLightMode
import com.expensetracker.viewmodel.ExpenseViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpenseList(viewModel: ExpenseViewModel, isDarkMode: Boolean) {
    val expenseList by viewModel.filteredExpensesStateFlow.collectAsState()
    val selectedCurrency by viewModel.currencyStateFlow.collectAsState()
    val listState = rememberLazyListState()

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(expenseList) {
        if (expenseList.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { paddingValues ->
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDarkMode) ListBackgroundColorDarkMode else ListBackgroundColorLightMode),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (expenseList.isNotEmpty()) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding())
                ) {
                    items(
                        items = expenseList,
                        key = { it.id }
                    ) { expense ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            positionalThreshold = { totalDistance -> totalDistance * 0.5f },
                            confirmValueChange = { value ->
                                if (value == SwipeToDismissBoxValue.EndToStart || value == SwipeToDismissBoxValue.StartToEnd) {
                                    viewModel.deleteExpense(expense)

                                    scope.launch {
                                        val result = snackBarHostState.showSnackbar(
                                            message = "Expense deleted",
                                            actionLabel = "Undo",
                                            duration = SnackbarDuration.Short
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            viewModel.insertExpense(
                                                expense.title,
                                                expense.amount,
                                                expense.date
                                            )
                                        }
                                    }
                                    true
                                } else {
                                    false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val progress = dismissState.progress // 0f → 1f
                                val animatedScale by animateFloatAsState(
                                    targetValue = if (progress > 0.1f) 1.2f else 0.7f, // icon grows as you swipe
                                    label = "iconScale"
                                )

                                val isLeftSwipe = dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Gray)
                                        .padding(14.dp),
                                    contentAlignment = if (isLeftSwipe) Alignment.CenterEnd else Alignment.CenterStart
                                ) {
                                    Row (
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color.White,
                                            modifier = Modifier
                                                .graphicsLayer {
                                                    scaleX = animatedScale
                                                    scaleY = animatedScale
                                                }
                                        )
                                    }
                                }
                            },
                            content = {
                                Box(
                                    modifier = Modifier.background(if (isDarkMode) ListBackgroundColorDarkMode else ListBackgroundColorLightMode)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = expense.title,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDarkMode) Color.White else Color.Black
                                        )
                                        Column(
                                            horizontalAlignment = Alignment.End
                                        ) {
                                            Text(
                                                text = "${selectedCurrency.firstOrNull() ?: ""} ${String.format("%.2f", expense.amount)}",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isDarkMode) Color.White else Color.Black
                                            )
                                            Text(
                                                text = formatDate(expense.date),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Light,
                                                color = if (isDarkMode) Color.White else Color.Black
                                            )
                                        }
                                    }
                                }
                            }
                        )
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = if (isDarkMode) Color.White else Color.DarkGray
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.no_data_found),
                        contentDescription = "No expenses",
                        modifier = Modifier.size(150.dp)
                    )
                    Text(
                        text = "No records to display",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkMode) Color.White else Color.DarkGray
                    )
                }
            }
        }
    }
}

fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(millis))
}