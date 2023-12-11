package br.itcampos.buildyourhealth.commom.snackbar

import android.content.res.Resources
import androidx.annotation.StringRes
import br.itcampos.buildyourhealth.R.string as AppText

sealed class SnackbarMessage {
    class StringSnackbar(val message: String) : SnackbarMessage()
    class ResourceSnackbar(@StringRes val message: Int) : SnackbarMessage()

    companion object {
        fun SnackbarMessage.toMessage(resouces: Resources): String {
            return when (this) {
                is StringSnackbar -> this.message
                is ResourceSnackbar -> resouces.getString(this.message)
            }
        }

        fun Throwable.toSnackbarMessage(): SnackbarMessage {
            val message = this.message.orEmpty()
            return if (message.isNotBlank()) StringSnackbar(message)
            else ResourceSnackbar(AppText.something_went_wrong)
        }
    }
}