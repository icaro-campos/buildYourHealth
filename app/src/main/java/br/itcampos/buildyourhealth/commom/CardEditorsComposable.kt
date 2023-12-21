package br.itcampos.buildyourhealth.commom

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CardEditors(
    setTrainingDateChange: (String) -> Unit
) {
    var isDatePickerVisible by remember { mutableStateOf(false) }
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }

    RegularCardEditor(
        "Data",
        Icons.Filled.DateRange,
        dateFormatter.format(selectedDate.time),
        Modifier.padding()
    ) {
        isDatePickerVisible = true
    }

    if (isDatePickerVisible) {
        ShowDatePickerDialog(selectedDate) { date ->
            selectedDate = date
            val formattedDate = dateFormatter.format(selectedDate.time)
            isDatePickerVisible = false

            setTrainingDateChange(formattedDate)
        }
    }
}

@Composable
fun ShowDatePickerDialog(initialDate: Calendar, onDateSelected: (Calendar) -> Unit) {
    val context = LocalContext.current
    val datePicker = remember { mutableStateOf(initialDate) }

    val onDateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        datePicker.value.set(Calendar.YEAR, year)
        datePicker.value.set(Calendar.MONTH, month)
        datePicker.value.set(Calendar.DAY_OF_MONTH, day)
        onDateSelected(datePicker.value)
    }

    DatePickerDialog(
        context,
        onDateSetListener,
        datePicker.value.get(Calendar.YEAR),
        datePicker.value.get(Calendar.MONTH),
        datePicker.value.get(Calendar.DAY_OF_MONTH)
    ).show()
}