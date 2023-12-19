package br.itcampos.buildyourhealth.screens.training

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.itcampos.buildyourhealth.commom.Result
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

            is TrainingScreenUiEvents.DeleteTraining -> TODO()
            is TrainingScreenUiEvents.OnChangeAddTrainingDialogState -> TODO()
            is TrainingScreenUiEvents.AddTraining -> TODO()
            TrainingScreenUiEvents.GetTrainings -> TODO()
            is TrainingScreenUiEvents.OnChangeTrainingDate -> TODO()
            is TrainingScreenUiEvents.OnChangeTrainingDescription -> TODO()
            is TrainingScreenUiEvents.OnChangeTrainingName -> TODO()
            is TrainingScreenUiEvents.SetTrainingToBeUpdated -> TODO()
            TrainingScreenUiEvents.UpdateTraining -> TODO()
        }
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
        private const val UTC = "UTC"
        private const val DATE_FORMAT = "EEE, d MMM yyyy"
    }
}