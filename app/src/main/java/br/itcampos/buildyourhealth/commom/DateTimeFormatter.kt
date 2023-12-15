package br.itcampos.buildyourhealth.commom

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getCurrentTimeAsString(): String {
    val currentTime = Date()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val currentTimeString = dateFormat.format(currentTime)
    return currentTimeString
}

fun convertDateFormat(dateString: String): String {
    val currentDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val newDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = currentDateFormat.parse(dateString)
    val newDateString = newDateFormat.format(date)
    return newDateString
}