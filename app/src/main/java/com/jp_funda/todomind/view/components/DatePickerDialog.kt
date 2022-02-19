package com.jp_funda.todomind.view.components

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.jp_funda.todomind.R
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import java.time.LocalDate

@Composable
fun DatePickerDialog(
    dateDialogState: MaterialDialogState,
    initialValue: LocalDate = LocalDate.now(),
    onSelected: (LocalDate) -> Unit,
) {
    var date: LocalDate = initialValue

    val colorTheme = DatePickerDefaults.colors(
        headerBackgroundColor = Color(ContextCompat.getColor(LocalContext.current, R.color.aqua)),
        headerTextColor = Color.White,
        activeBackgroundColor = Color.White,
        inactiveBackgroundColor = Color.Black,
        activeTextColor = Color.Black,
        inactiveTextColor = Color.White,
    )

    MaterialDialog(
        dialogState = dateDialogState,
        backgroundColor = Color(ContextCompat.getColor(LocalContext.current, R.color.aqua)),
        buttons = {
            positiveButton(
                "OK",
                textStyle = MaterialTheme.typography.button.copy(
                    color = Color(ContextCompat.getColor(LocalContext.current, R.color.teal_200)),
                ),
                onClick = { onSelected(date) }
            )
//                this.button("time", onClick = {
//                    dateDialogState.hide()
//                    timeDialogState.show()
//                })
            negativeButton(
                "Cancel",
                textStyle = MaterialTheme.typography.button.copy(
                    color = Color(ContextCompat.getColor(LocalContext.current, R.color.teal_200)),
                )
            )
        }
    ) {
        datepicker(
            colors = colorTheme,
            initialDate = initialValue
        ) {
            date = it
        }
    }
}