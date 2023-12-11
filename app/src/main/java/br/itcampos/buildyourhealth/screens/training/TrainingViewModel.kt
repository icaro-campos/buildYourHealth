package br.itcampos.buildyourhealth.screens.training

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import br.itcampos.buildyourhealth.R
import br.itcampos.buildyourhealth.R.string as AppText
import br.itcampos.buildyourhealth.Routes.TRAINING_ID
import br.itcampos.buildyourhealth.commom.ext.idFromParameter
import br.itcampos.buildyourhealth.commom.ext.isValidText
import br.itcampos.buildyourhealth.commom.snackbar.SnackbarManager
import br.itcampos.buildyourhealth.model.Training
import br.itcampos.buildyourhealth.model.service.AccountService
import br.itcampos.buildyourhealth.model.service.LogService
import br.itcampos.buildyourhealth.model.service.TrainingService
import br.itcampos.buildyourhealth.screens.BuildYourHealthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val trainingService: TrainingService,
    private val accountService: AccountService,
    logService: LogService
) : BuildYourHealthViewModel(logService) {
    val training = mutableStateOf(Training())

    init {
        val trainingId = savedStateHandle.get<String>(TRAINING_ID)
        Log.d("TrainingViewModel", "TrainingId: $trainingId")
        if (trainingId != null) {
            launchCatching {
                val loadedTraining = trainingService.getTrainingById(trainingId.idFromParameter())
                Log.d("TrainingViewModel", "LoadedTraining: $loadedTraining")
                training.value = loadedTraining ?: Training()
            }
        }
    }

    fun onNameChange(newValue: String) {
        training.value = training.value.copy(name = newValue)
    }

    fun onDescriptionChange(newValue: String) {
        training.value = training.value.copy(description = newValue)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onDateChange(newValue: Long) {
        val newDate = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(Date(newValue))
        training.value = training.value.copy(date = newDate)
    }

    fun onDoneClick(popUpScreen: () -> Unit) {
        if (!training.value.name.isNotBlank()) {
            SnackbarManager.showMessage(AppText.insert_training_name)
            return
        }
        if (!training.value.description.isValidText()) {
            SnackbarManager.showMessage(AppText.insert_training_description)
            return
        }
        if (!training.value.date.isValidText()) {
            SnackbarManager.showMessage(AppText.insert_training_date)
            return
        }

        launchCatching {
            val editedTask = training.value
            val userId = accountService.currentUserId.takeIf { it.isNotBlank() }
            userId?.let {
                editedTask.userId = it
                if (editedTask.id.isBlank()) {
                    trainingService.addTraining(editedTask)
                } else {
                    trainingService.addTraining(editedTask)
                }
            }
            popUpScreen()
        }
    }

    private fun loadTrainingDetails(trainingId: String) {
        launchCatching {
            val loadedTraining = trainingService.getTrainingById(trainingId)
            training.value = loadedTraining?.copy() ?: Training()
        }
    }

    companion object {
        private const val UTC = "UTC"
        private const val DATE_FORMAT = "EEE, d MMM yyyy"
    }
}