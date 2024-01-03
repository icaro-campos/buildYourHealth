package br.itcampos.buildyourhealth.screens.training

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.itcampos.buildyourhealth.commom.Result
import br.itcampos.buildyourhealth.model.Training
import br.itcampos.buildyourhealth.model.service.TrainingService
import br.itcampos.buildyourhealth.navigation.Routes.TRAINING_ID
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
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
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

    init {
        val trainingId = savedStateHandle.get<String>(TRAINING_ID)
        Log.d(TAG, "Training ID from SavedStateHandle: $trainingId")
        val oldState = state.value
        if (trainingId != null) {
            getTrainingDetails(oldState, trainingId)
        }
    }

    private fun reduce(event: TrainingScreenUiEvents, oldState: TrainingScreenUiState) {
        when (event) {

            is TrainingScreenUiEvents.GetTrainingDetails -> {
                getTrainingDetails(oldState = oldState, trainingId = event.trainingId)
            }

            TrainingScreenUiEvents.GetTrainings -> {
                getAllTasks(oldState = oldState)
            }

            is TrainingScreenUiEvents.OnChangeTrainingDate -> {
                onChangeTrainingDate(oldState = oldState, date = event.date)
            }

            is TrainingScreenUiEvents.OnChangeTrainingDescription -> {
                onChangeTrainingDescription(oldState = oldState, description = event.description)
            }

            is TrainingScreenUiEvents.OnChangeTrainingName -> {
                onChangeTrainingName(oldState = oldState, name = event.name)
            }

            is TrainingScreenUiEvents.SetTrainingToBeUpdated -> {
                setTrainingToBeUpdated(
                    oldState = oldState,
                    training = event.trainingToBeUpdated
                )
            }

            TrainingScreenUiEvents.UpdateTraining -> {
                updateTraining(oldState = oldState)
            }

            is TrainingScreenUiEvents.OnChangeUpdateTrainingDialogState -> {
                onChangeUpdateTrainingDialogState(
                    oldState = oldState,
                    isShown = event.show
                )
            }
        }
    }

    private fun getAllTasks(oldState: TrainingScreenUiState) {
        viewModelScope.launch {
            setState(oldState.copy(isLoading = true))
            when (val result = trainingService.getAllTrainings()) {
                is Result.Failure -> {
                    setState(oldState.copy(isLoading = false))
                    val errorMessage = result.exception.message ?: "Um erro ao buscar os Treinos"
                    setEffects { ScreenUiSideEffect.ShowSnackbarMessage(message = errorMessage) }
                }

                is Result.Success -> {
                    setState(oldState.copy(isLoading = false, trainings = result.data))
                }
            }
        }
    }

    private fun updateTraining(oldState: TrainingScreenUiState) {
        viewModelScope.launch {
            setState(
                oldState.copy(
                    isLoading = true
                )
            )

            val name = oldState.currentTextFieldName
            val description = oldState.currentTextFieldDescription
            val date = oldState.currentTextFieldDate
            val trainingToBeUpdated = oldState.trainingToBeUpdated

            when (val result = trainingService.updateTraining(
                name = name,
                description = description,
                date = date,
                trainingId = trainingToBeUpdated?.trainingId ?: ""
            )
            ) {
                is Result.Failure -> {
                    setState(
                        oldState.copy(
                            isLoading = false
                        )
                    )

                    val errorMessage =
                        result.exception.message ?: "Um erro ocorreu ao atualizar o Treino"

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

                    sendEvent(
                        event = TrainingScreenUiEvents.OnChangeUpdateTrainingDialogState(
                            show = false
                        )
                    )

                    setEffects { ScreenUiSideEffect.ShowSnackbarMessage(message = "Treino atualizado com sucesso!") }

                    sendEvent(TrainingScreenUiEvents.GetTrainings)

                    val updatedTrainingId = oldState.trainingToBeUpdated?.trainingId
                    sendEvent(TrainingScreenUiEvents.GetTrainingDetails(updatedTrainingId ?: ""))
                }
            }
        }



    }

    private fun setTrainingToBeUpdated(oldState: TrainingScreenUiState, training: Training) {
        setState(
            oldState.copy(
                trainingToBeUpdated = training
            )
        )
    }

    private fun onChangeUpdateTrainingDialogState(
        oldState: TrainingScreenUiState,
        isShown: Boolean
    ) {
        setState(
            oldState.copy(
                isShowUpdateTrainingDialog = isShown
            )
        )
    }

    private fun onChangeTrainingDate(oldState: TrainingScreenUiState, date: String) {
        setState(oldState.copy(currentTextFieldDate = date))
    }

    private fun onChangeTrainingDescription(oldState: TrainingScreenUiState, description: String) {
        setState(oldState.copy(currentTextFieldDescription = description))
    }

    private fun onChangeTrainingName(oldState: TrainingScreenUiState, name: String) {
        setState(oldState.copy(currentTextFieldName = name))
    }

    private fun getTrainingDetails(oldState: TrainingScreenUiState, trainingId: String) {
        viewModelScope.launch {
            setState(
                oldState.copy(
                    isLoading = true
                )
            )

            when (val result = trainingService.getTrainingDetails(trainingId)) {
                is Result.Failure -> {
                    setState(state.value.copy(isLoading = false))
                    val errorMessage =
                        result.exception.message ?: "Um erro ao buscar os detalhes do Treino"
                    setEffects { ScreenUiSideEffect.ShowSnackbarMessage(message = errorMessage) }
                }

                is Result.Success -> {
                    setState(oldState.copy(isLoading = false, trainingDetails = result.data))
                }
            }
        }
    }

    fun closeView(popUpScreen: () -> Unit) {
        popUpScreen()
    }

    companion object {
        private const val TAG = "TrainingViewModel"
    }
}