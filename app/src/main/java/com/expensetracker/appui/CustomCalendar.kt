package com.expensetracker.appui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.expensetracker.R
import com.expensetracker.ui.theme.ListBackgroundColorDarkMode
import com.expensetracker.ui.theme.ListBackgroundColorLightMode
import com.expensetracker.ui.theme.RedDarkGradient
import com.expensetracker.ui.theme.RedLightGradient
import com.kizitonwose.calendar.core.yearMonth
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomCalendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    isDarkMode: Boolean
) {
    Log.d("TAG", "CustomCalendar: custom calendar")
    val today = remember { LocalDate.now() }
    val startDate = remember { today.minusMonths(24) }
    val endDate = remember { today.plusMonths(24) }
    val coroutineScope = rememberCoroutineScope()

    val allDates = remember {
        generateSequence(startDate) { it.plusDays(1) }
            .takeWhile { it <= endDate }
            .toList()
    }

    val listState = rememberLazyListState()

    var currentMonth by remember { mutableStateOf(selectedDate.yearMonth) }

    // Keep month in sync with selectedDate
    LaunchedEffect(selectedDate) {
        currentMonth = selectedDate.yearMonth
    }

    // Scroll to today on first composition
    LaunchedEffect(Unit) {
        val index = allDates.indexOf(today)
        if (index != -1) {
            // Calculate center offset (5 items approx. visible, adjust based on screen size)
            val centerOffset = 2
            val targetIndex = (index - centerOffset).coerceAtLeast(0)
            listState.scrollToItem(targetIndex)
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress to listState.firstVisibleItemIndex }
            .collect { (isScrolling, index) ->
                if (isScrolling && index in allDates.indices) {
                    val visibleDate = allDates[index]
                    currentMonth = visibleDate.yearMonth
                }
            }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDarkMode) ListBackgroundColorDarkMode else ListBackgroundColorLightMode)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Month Header with arrows
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(id = R.drawable.left_arrow),
                    contentDescription = "Previous Month",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            coroutineScope.launch {
                                val newMonth = currentMonth.minusMonths(1)
                                val firstDateOfMonth = newMonth.atDay(1)
                                val index = allDates.indexOf(firstDateOfMonth)
                                if (index != -1) {
                                    listState.scrollToItem(index)
                                    currentMonth = newMonth
                                }
                            }
                        }
                )

                Text(
                    text = "${
                        currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }
                    } - ${currentMonth.year}",
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.White else Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                Image(
                    painter = painterResource(id = R.drawable.right_arrow),
                    contentDescription = "Next Month",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            coroutineScope.launch {
                                val newMonth = currentMonth.plusMonths(1)
                                val firstDateOfMonth = newMonth.atDay(1)
                                val index = allDates.indexOf(firstDateOfMonth)
                                if (index != -1) {
                                    listState.scrollToItem(index)
                                    currentMonth = newMonth
                                }
                            }
                        }
                )
            }

            LazyRow(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
            ) {
                itemsIndexed(allDates) { _, date ->
                    val isSelected = date == selectedDate
                    val dayOfWeek = date.dayOfWeek.getDisplayName(
                        java.time.format.TextStyle.SHORT,
                        Locale.getDefault()
                    ).take(2).replaceFirstChar { it.uppercase() }

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .width(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Weekday label
                        Text(
                            text = dayOfWeek,
                            color = if (isDarkMode) Color.White else Color.Black,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )

                        // Date box
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    brush = if (isSelected) {
                                        Brush.linearGradient(
                                            colors = listOf(RedDarkGradient, RedLightGradient)
                                        )
                                    } else {
                                        Brush.linearGradient(
                                            listOf(Color.Transparent, Color.Transparent)
                                        )
                                    }
                                )
                                .clickable {
                                    onDateSelected(date)
                                    currentMonth = date.yearMonth
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                color = if (isSelected) Color.White else if (isDarkMode) Color.White else Color.Black,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}