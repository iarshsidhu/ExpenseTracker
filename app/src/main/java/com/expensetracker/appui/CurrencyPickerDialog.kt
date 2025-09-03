package com.expensetracker.appui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.expensetracker.ui.theme.AppBackgroundColorDarkMode
import com.expensetracker.ui.theme.AppBackgroundColorLightMode
import com.expensetracker.ui.theme.ListBackgroundColorDarkMode
import com.expensetracker.ui.theme.ListBackgroundColorLightMode

@Composable
fun CurrencyPickerDialog(
    currencies: List<String>,
    onDismiss: () -> Unit,
    onCurrencySelected: (String) -> Unit,
    isDarkMode: Boolean
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = if (isDarkMode) ListBackgroundColorDarkMode else ListBackgroundColorLightMode)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Currency",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.White else Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(currencies) { currency ->
                        Text(
                            text = currency,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDarkMode) Color.White else Color.Black,
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp).clickable {
                                onCurrencySelected(currency)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}