package br.itcampos.buildyourhealth.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.itcampos.buildyourhealth.commom.Result
import br.itcampos.buildyourhealth.model.Training
import br.itcampos.buildyourhealth.model.service.TrainingService
import br.itcampos.buildyourhealth.navigation.Routes.TRAINING_SCREEN
import br.itcampos.buildyourhealth.ui.events.HomeScreenUiEvents
import br.itcampos.buildyourhealth.ui.side_effects.ScreenUiSideEffect
import br.itcampos.buildyourhealth.ui.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val trainingService: TrainingService
) : ViewModel() {

    private val _state: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _effect: Channel<ScreenUiSideEffect> = Channel()
    val effect = _effect.receiveAsFlow()

    fun sendEvent(event: HomeScreenUiEvents) {
        reduce(event = event, oldState = state.value)
    }

    private fun setState(newState: HomeUiState) {
        _state.value = newState
    }

    private fun setEffects(builder: () -> ScreenUiSideEffect) {
        val effectValue = builder()
        viewModelScope.launch {
            _effect.send(effectValue)
        }
    }

    init {
        sendEvent(HomeScreenUiEvents.GetTrainings)
    }

    private fun reduce(
        event: HomeScreenUiEvents,
        oldState: HomeUiState,

    ) {
        when (event) {
            is HomeScreenUiEvents.DeleteTraining -> {
                deleteTraining(oldState = oldState, trainingId = event.trainingId)
            }

            HomeScreenUiEvents.GetTrainings -> {
                getAllTrainings(oldState = oldState)
            }
        }
    }

    private fun getAllTrainings(oldState: HomeUiState) {
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

    private fun deleteTraining(oldState: HomeUiState, trainingId: String) {
        viewModelScope.launch {
            setState(
                oldState.copy(
                    isLoading = true
                )
            )

            when (val result = trainingService.deleteTraining(trainingId)) {
                is Result.Failure -> {
                    setState(
                        oldState.copy(
                            isLoading = false
                        )
                    )

                    val errorMessage =
                        result.exception.message ?: "Um erro ocorreu ao deletar a Tarefa"

                    setEffects { ScreenUiSideEffect.ShowSnackbarMessage(message = errorMessage) }
                }

                is Result.Success -> {
                    setState(
                        oldState.copy(
                            isLoading = false
                        )
                    )

                    setEffects { ScreenUiSideEffect.ShowSnackbarMessage(message = "Tarefa deletada com sucesso") }

                    sendEvent(HomeScreenUiEvents.GetTrainings)
                }
            }
        }
    }

    fun onAddNewTrainingClick(openScreen: (String) -> Unit) = openScreen(
        TRAINING_SCREEN)

    /*fun onTrainingClick(openScreen: (String) -> Unit, training: Training, action: String) {
        Log.d("HomeViewModel", "TrainingId passed: ${training.id}")
        when (HomeActionOption.getByTitle(action)) {
            HomeActionOption.EditTraining -> openScreen("$TRAINING_SCREEN/${training.id}")
            HomeActionOption.DeleteTraining -> onDeleteTrainingScreen(training)
        }
    }

    private fun onDeleteTrainingScreen(training: Training) {
        launchCatching { trainingService.deleteTraining(training.id) }
    }*/

    companion object {
        private const val TAG = "HomeViewModel"
    }
}