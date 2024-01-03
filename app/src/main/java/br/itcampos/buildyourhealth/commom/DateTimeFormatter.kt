package br.itcampos.buildyourhealth.commom

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun convertDateFormat(dateString: String): String {
    val currentDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val newDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = currentDateFormat.parse(dateString)
    return newDateFormat.format(date)
}