package br.itcampos.buildyourhealth.screens.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import br.itcampos.buildyourhealth.Routes.TRAINING_ID
import br.itcampos.buildyourhealth.Routes.TRAINING_SCREEN
import br.itcampos.buildyourhealth.model.Training
import br.itcampos.buildyourhealth.model.service.AccountService
import br.itcampos.buildyourhealth.model.service.LogService
import br.itcampos.buildyourhealth.model.service.TrainingService
import br.itcampos.buildyourhealth.model.service.UserStorageService
import br.itcampos.buildyourhealth.screens.BuildYourHealthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val trainingService: TrainingService,
    logService: LogService
) : BuildYourHealthViewModel(logService) {

    val trainings = trainingService.trainings

    fun onAddNewTrainingClick(openScreen: (String) -> Unit) = openScreen(TRAINING_SCREEN)

    fun onTrainingClick(openScreen: (String) -> Unit, training: Training, action: String) {
        Log.d("HomeViewModel", "TrainingId passed: ${training.id}")
        when (HomeActionOption.getByTitle(action)) {
            HomeActionOption.EditTraining -> openScreen("$TRAINING_SCREEN/${training.id}")
            HomeActionOption.DeleteTraining -> onDeleteTrainingScreen(training)
        }
    }

    private fun onDeleteTrainingScreen(training: Training) {
        launchCatching { trainingService.deleteTraining(training.id) }
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}