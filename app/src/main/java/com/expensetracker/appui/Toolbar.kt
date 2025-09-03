package com.expensetracker.appui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CurrencyPound
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.CurrencyYen
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expensetracker.ui.theme.AppBackgroundColorDarkMode
import com.expensetracker.ui.theme.AppBackgroundColorLightMode
import com.expensetracker.viewmodel.ExpenseViewModel

@Composable
fun ExpenseToolbar(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    viewModel: ExpenseViewModel
) {
    var showCurrencyDialog by remember { mutableStateOf(false) }
    val selectedCurrency by viewModel.currencyStateFlow.collectAsState()

    val currencyIcon = when (selectedCurrency) {
        "₹ - Indian Rupee" -> Icons.Default.CurrencyRupee
        "$ - US Dollar" -> Icons.Default.AttachMoney
        "€ - Euro" -> Icons.Default.Euro
        "£ - British Pound" -> Icons.Default.CurrencyPound
        "¥ - Japanese Yen" -> Icons.Default.CurrencyYen
        else -> Icons.Default.CurrencyRupee
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(56.dp),
        color = if (isDarkMode) AppBackgroundColorDarkMode else AppBackgroundColorLightMode
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Total Expenses",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color.White else Color.Black
            )

            IconButton(
                onClick = { showCurrencyDialog = true },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = currencyIcon,
                    contentDescription = "Change Currency",
                    tint = if (isDarkMode) Color.White else Color.Black
                )
            }

            IconButton(onClick = onToggleTheme, modifier = Modifier.align(Alignment.CenterEnd)) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.WbSunny else Icons.Default.NightsStay,
                    contentDescription = "Toggle Theme",
                    tint = if (isDarkMode) Color.White else Color.Black
                )
            }
        }
    }

    if (showCurrencyDialog) {
        CurrencyPickerDialog(
            currencies = listOf(
                "₹ - Indian Rupee",
                "$ - US Dollar",
                "€ - Euro",
                "£ - British Pound",
                "¥ - Japanese Yen"
            ),
            onDismiss = { showCurrencyDialog = false },
            onCurrencySelected = { currency ->
                viewModel.updateCurrency(currency)
            },
            isDarkMode = isDarkMode
        )
    }
}