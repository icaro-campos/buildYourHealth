package br.itcampos.buildyourhealth.screens.training

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import br.itcampos.buildyourhealth.commom.Result
import br.itcampos.buildyourhealth.commom.ext.isValidText
import br.itcampos.buildyourhealth.commom.snackbar.SnackbarManager
import br.itcampos.buildyourhealth.model.Training
import br.itcampos.buildyourhealth.model.service.TrainingService
import br.itcampos.buildyourhealth.ui.events.TrainingScreenUiEvents
import br.itcampos.buildyourhealth.ui.side_effects.ScreenUiSideEffect
import br.itcampos.buildyourhealth.ui.state.TrainingScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import br.itcampos.buildyourhealth.R.string as AppText

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val trainingService: TrainingService
) : ViewModel() {

    private val _state: MutableStateFlow<TrainingScreenUiState> =
        MutableStateFlow(TrainingScreenUiState())
    val state: StateFlow<TrainingScreenUiState> = _state.asStateFlow()

    private val _effect: Channel<ScreenUiSideEffect> = Channel()
    val effect = _effect.receiveAsFlow()

    fun sendEvent(event: TrainingScreenUiEvents) {
        reduce(event = event, oldState = state.value)
    }

    private fun setState(newState: TrainingScreenUiState) {
        _state.value = newState
    }

    private fun setEffects(buiilder: () -> ScreenUiSideEffect) {
        val effectValue = buiilder()
        viewModelScope.launch { _effect.send(effectValue) }
    }

    private fun reduce(event: TrainingScreenUiEvents, oldState: TrainingScreenUiState) {
        when (event) {
            is TrainingScreenUiEvents.AddTraining -> {
                addTraining(
                    oldState = oldState,
                    name = event.name,
                    description = event.description,
                    date = event.date
                )
            }

            is TrainingScreenUiEvents.OnChangeTrainingDescription -> {
                onChangeTrainingDescription(oldState = oldState, description = event.description)
            }

            is TrainingScreenUiEvents.OnChangeTrainingName -> {
                onChangeTrainingName(oldState = oldState, name = event.name)
            }

            is TrainingScreenUiEvents.OnChangeTrainingDate -> {
                onChangeTrainingDate(oldState = oldState, date = event.date)
            }

            is TrainingScreenUiEvents.SetTrainingToBeUpdated -> {
                setTrainingToBeUpdated(oldState = oldState, training = event.trainingToBeUpdated)
            }

            TrainingScreenUiEvents.UpdateTraining -> {
                updateTask(oldState = oldState)
            }

        }
    }

    fun closeView(popUpScreen: () -> Unit) {
        popUpScreen()
    }

    private fun updateTask(oldState: TrainingScreenUiState) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))

            val name = oldState.currentTextFieldName
            val description = oldState.currentTextFieldDescription
            val date = oldState.currentTextFieldDate
            val trainingToBeUpdated = oldState.trainingToBeUpdated

            when (val result = trainingService.updateTraining(
                name = name,
                description = description,
                date = date,
                trainingId = trainingToBeUpdated?.trainingId ?: ""
            )) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))

                    val errorMessage =
                        result.exception.message ?: "Um erro ocorreu ao atualizar a Tarefa."

                    setEffects { ScreenUiSideEffect.ShowSnackbarMessage(message = errorMessage) }
                }

                is Result.Success -> {
                    setState(
                        oldState.copy(
                            isLoading = false,
                            currentTextFieldName = "",
                            currentTextFieldDescription = "",
                            currentTextFieldDate = ""
                        )
                    )

                    setEffects { ScreenUiSideEffect.ShowSnackbarMessage(message = "Tarefa atualizada com sucesso.") }
                }
            }
        }
    }

    private fun setTrainingToBeUpdated(oldState: TrainingScreenUiState, training: Training) {
        setState(oldState.copy(trainingToBeUpdated = training))
    }

    private fun onChangeTrainingDate(oldState: TrainingScreenUiState, date: String) {
        setState(oldState.copy(currentTextFieldDate = date))
    }

    private fun onChangeTrainingName(oldState: TrainingScreenUiState, name: String) {
        setState(oldState.copy(currentTextFieldName = name))
    }

    private fun onChangeTrainingDescription(oldState: TrainingScreenUiState, description: String) {
        setState(oldState.copy(currentTextFieldDescription = description))
    }

    private fun addTraining(
        oldState: TrainingScreenUiState,
        name: String,
        description: String,
        date: String
    ) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))

            when (val result =
                trainingService.addTraining(name = name, description = description, date = date)) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))

                    val errorMessage =
                        result.exception.message ?: "Um erro ocorreu ao adicionar o Treino"

                    setEffects { ScreenUiSideEffect.ShowSnackbarMessage(message = errorMessage) }
                }

                is Result.Success -> {
                    setState(
                        oldState.copy(
                            isLoading = false,
                            currentTextFieldName = "",
                            currentTextFieldDescription = "",
                            currentTextFieldDate = ""
                        )
                    )

                    setEffects { ScreenUiSideEffect.ShowSnackbarMessage(message = "Tarefa adicionanda com sucesso") }
                }
            }
        }
    }

    /*init {
        val trainingId = savedStateHandle.get<String>(TRAINING_ID)
        Log.d("TrainingViewModel", "TrainingId: $trainingId")
        if (trainingId != null) {
            viewModelScope.launch {
                val loadedTraining = trainingService.updateTraining(trainingId)
                Log.d("TrainingViewModel", "LoadedTraining: $loadedTraining")
                training.value = loadedTraining ?: Training()
            }
        }
    }*/

    companion object {
        private const val UTC = "UTC"
        private const val DATE_FORMAT = "EEE, d MMM yyyy"
    }
}