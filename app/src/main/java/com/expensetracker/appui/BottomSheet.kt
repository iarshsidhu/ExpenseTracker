package com.expensetracker.appui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.expensetracker.ui.theme.ListBackgroundColorDarkMode
import com.expensetracker.ui.theme.ListBackgroundColorLightMode
import com.expensetracker.ui.theme.RedDarkGradient
import com.expensetracker.ui.theme.RedLightGradient

@Composable
fun BottomSheetContent(
    amountText: String,
    descriptionText: String,
    onAmountChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSave: () -> Unit,
    isDarkMode: Boolean
) {
    var amountError by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add Expense",
            color = if (isDarkMode) Color.White else Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = amountText,
            onValueChange = {
                onAmountChange(it)
                amountError = false
            },
            label = { Text(text = "Amount", color = if (isDarkMode) Color.White else Color.DarkGray) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),
            trailingIcon = {
                if (amountError) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "error",
                        tint = Color.Red
                    )
                }
            },
            shape = RoundedCornerShape(8.dp),
            textStyle = TextStyle(color = if (isDarkMode) Color.White else Color.Black),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                capitalization = KeyboardCapitalization.None,
                imeAction = ImeAction.Next
            ),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = if (isDarkMode) ListBackgroundColorDarkMode else ListBackgroundColorLightMode,
                focusedContainerColor = if (isDarkMode) ListBackgroundColorDarkMode else ListBackgroundColorLightMode,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = if (isDarkMode) Color.White else Color.Black
            )
        )
        Spacer(modifier = Modifier.height(14.dp))
        TextField(
            value = descriptionText,
            onValueChange = {
                onDescriptionChange(it)
                descriptionError = false
            },
            label = { Text(text = "Description", color = if (isDarkMode) Color.White else Color.DarkGray) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),
            trailingIcon = {
                if (descriptionError) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "error",
                        tint = Color.Red
                    )
                }
            },
            shape = RoundedCornerShape(8.dp),
            textStyle = TextStyle(color = if (isDarkMode) Color.White else Color.Black),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done
            ),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = if (isDarkMode) ListBackgroundColorDarkMode else ListBackgroundColorLightMode,
                focusedContainerColor = if (isDarkMode) ListBackgroundColorDarkMode else ListBackgroundColorLightMode,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = if (isDarkMode) Color.White else Color.Black
            )
        )
        Spacer(modifier = Modifier.height(14.dp))
        SaveButton {
            amountError = amountText.isBlank()
            descriptionError = descriptionText.isBlank()

            if (!amountError && !descriptionError) {
                onSave()
            }
        }
    }
}

@Composable
fun SaveButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
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
            text = "SAVE",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}